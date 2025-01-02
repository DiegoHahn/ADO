package com.thomsonreuters.ado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;

@SpringBootApplication
@EnableScheduling
public class AdoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdoApplication.class, args);
	}

	@Bean
	public AzureDevOpsClient azureDevOpsClient(AzureDevOpsAuthenticator authenticator) {
		String organizationUrl = "https://dev.azure.com/diego29122/";
		String analyticsOrganizationUrl = "https://analytics.dev.azure.com/diego29122/";
		return new AzureDevOpsClient(organizationUrl, authenticator, analyticsOrganizationUrl);
	}
}