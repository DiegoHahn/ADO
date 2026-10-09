package io.github.diegohahn.ado;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.diegohahn.ado.authentication.AzureDevOpsAuthenticator;
import io.github.diegohahn.ado.client.AzureDevOpsClient;

@SpringBootApplication
@EnableScheduling
public class AdoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdoApplication.class, args);
	}

	@Bean
	public AzureDevOpsClient azureDevOpsClient(
			AzureDevOpsAuthenticator authenticator,
			@Value("${ado.organization-url}") String organizationUrl,
			@Value("${ado.analytics-organization-url}") String analyticsOrganizationUrl) {
		return new AzureDevOpsClient(organizationUrl, authenticator, analyticsOrganizationUrl);
	}
}