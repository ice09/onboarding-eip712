package de.ice09.safesigs.services;

import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class HttpServices {

    public String readEthereumJson(String name) {
        try (InputStream in = new URL( "https://"+ name + ".keybase.pub/invite_me.json" ).openStream()) {
            return IOUtils.toString( in, Charset.forName("UTF-8"));
        } catch (Exception ex) {
            return "";
        }
    }


    public String readKeybaseJson(String name) {
        try (InputStream in = new URL( "https://keybase.io/_/api/1.0/user/lookup.json?usernames=" + name + "&fields=proofs_summary" ).openStream()) {
            return IOUtils.toString( in, Charset.forName("UTF-8"));
        } catch (Exception ex) {
            return "";
        }
    }}
