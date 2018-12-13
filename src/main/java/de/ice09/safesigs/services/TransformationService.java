package de.ice09.safesigs.services;

import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationService {

    public boolean checkCredentials(String json) {
        List<String> names = JsonPath.read(json, "$..by_proof_type.twitter..nametag");
        boolean checkTwitter = !names.isEmpty();
        names = JsonPath.read(json, "$..by_proof_type.github..nametag");
        boolean checkGithub = !names.isEmpty();
        names = JsonPath.read(json, "$..by_proof_type.reddit..nametag");
        boolean checkReddit = !names.isEmpty();
        // is either GH, Twitter or Reddit proof present?
        return checkGithub || checkReddit || checkTwitter;
    }

    public Map<String, String> readEthereumJson(String json) {
        Map<String, String> values = new HashMap<>();
        values.put("proof", JsonPath.read(json, "$.proof").toString());
        values.put("address", JsonPath.read(json, "$.address").toString());
        values.put("signature", JsonPath.read(json, "$.signature").toString());
        values.put("username", JsonPath.read(json, "$.user").toString());
        return values;
    }
}