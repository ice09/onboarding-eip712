package de.ice09.safesigs.controller;

import de.ice09.safesigs.services.HttpServices;
import de.ice09.safesigs.services.SignatureService;
import de.ice09.safesigs.services.TransformationService;
import de.ice09.safesigs.services.VerifierService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class VerifierController {

    private VerifierService verifierService;
    private HttpServices http = new HttpServices();
    private TransformationService transform = new TransformationService();
    private SignatureService signatureService = new SignatureService();
    private final Web3j web3;

    public VerifierController(Web3j web3) {
        this.verifierService = new VerifierService();
        this.web3 = web3;
    }

    @RequestMapping("/verify")
    public String verify(@RequestParam(value="user") String user) throws Exception {
        if (!transform.checkCredentials(http.readKeybaseJson(user))) {
            throw new IllegalStateException("No Credentials found, make sure user has Twitter OR Github OR Reddit Credentials.");
        }
        Map<String, String> values = transform.readEthereumJson(http.readEthereumJson(user));
        byte[] proof = Numeric.hexStringToByteArray(signatureService.createEIP712Proof(Hash.sha3(values.get("username").getBytes()), Hash.sha3(values.get("proof").getBytes()), values.get("address")));
        String receiver = verifierService.ecrecoverAddress(proof, Numeric.hexStringToByteArray(values.get("signature")), values.get("address"));
        if ((receiver == null ) || !receiver.equals(values.get("address").substring(2))) {
            throw new IllegalStateException("ecrecovered address does not match JSON address.");
        }
        String privateKey1 = "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";
        Credentials credentials = Credentials.create(privateKey1);

        TransactionReceipt transactionReceipt = Transfer.sendFunds(web3, credentials, receiver, BigDecimal.valueOf(10.0), Convert.Unit.ETHER).send();
        return transactionReceipt.getTransactionHash();
    }
}
