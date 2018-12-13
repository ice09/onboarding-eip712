package de.ice09.safesigs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@SpringBootApplication
@Configuration
public class Application implements CommandLineRunner {

    private static Log log = LogFactory.getLog(Application.class);
    private Web3j web3j;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Web3j web3() {
        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
        return web3j;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
    }
}
