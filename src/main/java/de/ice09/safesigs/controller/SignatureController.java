package de.ice09.safesigs.controller;

import de.ice09.safesigs.services.SignatureService;
import de.ice09.safesigs.services.VerifierService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SignatureController {

    private SignatureService signatureService = new SignatureService();
    private VerifierService verifierService = new VerifierService();

    @RequestMapping("/debug")
    public String debug(@RequestParam(value="user") String user, @RequestParam(value="address") String address, @RequestParam(value="submittedSig") String submittedSig) throws Exception {
        byte[] userHash = Hash.sha3(user.getBytes());
        byte[] proofHash = Hash.sha3(("I am " + user + " verifying address " + address.toLowerCase()).getBytes());
        String signatureForMainAcct = signatureService.sign(userHash, proofHash, address);
        if (!signatureForMainAcct.equals(submittedSig)) {
            throw new IllegalStateException("Submitted signature does not equal calculated signature.");
        }
        byte[] proof = Numeric.hexStringToByteArray(signatureService.createEIP712Proof(userHash, proofHash, address));
        String ecrecovered = verifierService.ecrecoverAddress(proof, Numeric.hexStringToByteArray(signatureForMainAcct), address);
        if (ecrecovered.toLowerCase().equals(address.toLowerCase())) {
            throw new IllegalStateException("Submitted address does not match ecrecovered address.");
        }
        return Numeric.toHexStringNoPrefix(proof);
    }
}
