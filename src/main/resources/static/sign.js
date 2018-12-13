function genSolidityVerifier(signature, user, address, proof) {
	return json.replace("<USER>", user)
	  .replace("<ADDRESS>", address)
    .replace("<SIGNATURE>", signature)
    .replace("<PROOF>", proof);
}

window.onload = function (e) {
  var text = document.getElementById("info");
  text.style.display = "none";
  var res = document.getElementById("pre");
  res.style.display = "none";

  // force the user to unlock their MetaMask
  if (web3.eth.accounts[0] == null) {
    alert("Please unlock MetaMask first");
  }

  var signBtn = document.getElementById("create");
  signBtn.onclick = function(e) {
    if (web3.eth.accounts[0] == null) {
      return;
    }
    signBtn.disabled = true;

    const domain = [
      { name: "name", type: "string" },
      { name: "version", type: "string" },
      { name: "chainId", type: "uint256" },
      { name: "verifyingContract", type: "address" },
      { name: "salt", type: "bytes32" }
    ];

    const akyc = [
      { name: "user", type: "string" },
      { name: "proof", type: "string" },
      { name: "useraddress", type: "address" }
    ];

    const domainData = {
      name: "invite_me Demo",
      version: "2",
      chainId: 5777,
      verifyingContract: "0x1C56346CD2A2Bf3202F771f50d3D14a367B48070",
      salt: "0xf2d857f4a3edcb9b78b4d503bfe733db1e3f6cdc2b7971ee739626c97e86a558"
    };

    var user = document.getElementById("usr").value;

    var message = {
      user: user,
      proof: "I am " + user + " verifying address " + web3.eth.accounts[0].toLowerCase(),
      useraddress: web3.eth.accounts[0].toLowerCase()
    };
    
    const data = JSON.stringify({
      types: {
        EIP712Domain: domain,
        AKYC: akyc
      },
      domain: domainData,
      primaryType: "AKYC",
      message: message
    });

    const signer = web3.eth.accounts[0];
    console.log("signer:" + signer);

    web3.currentProvider.sendAsync(
      {
        method: "eth_signTypedData_v3",
        params: [signer, data],
        from: signer
      }, 
      function(err, result) {
        if (err || result.error) {
          return console.error(result);
        }
        console.log(data);
        console.log(result.result.substring(2));
        const signatureString = result.result.substring(2);

        const proof = ("I am " + user + " verifying address " + web3.eth.accounts[0].toLowerCase())
        res.style.display = "block";
        res.innerHTML =  "<br/><p><span class=\"text-primary\">Copy this code snippet into the local file <code>KBFS/public/" + document.getElementById("usr").value + "/invite_me.json</code></span></p></div>";
        text.style.display = "block";

        text.innerHTML = "<button class=\"btn btn-secondary\" data-clipboard-target=\"#code\">Copy JSON to clipboard</button><br/><br/><textarea id='code' rows='6' cols='155'>" + genSolidityVerifier('0x' + signatureString, document.getElementById("usr").value, web3.eth.accounts[0], proof) + "</textarea>";
        document.getElementById("trx").innerHTML='<button id="verify" type="button" class="btn btn-primary">Verify user and request 10 ETH</button>';
      }
    );
  };
}
