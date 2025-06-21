package com.user.wallet.user.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
//@EnableElasticsearchRepositories
public class UserWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserWalletApplication.class, args);
	}

}
