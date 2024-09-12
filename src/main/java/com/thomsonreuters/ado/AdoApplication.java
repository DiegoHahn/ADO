package com.thomsonreuters.ado;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.thomsonreuters.ado.service.WorkItemService.UpdateWorkItemQuery;

@SpringBootApplication
public class AdoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdoApplication.class, args);
	}

	@Bean
	public AzureDevOpsAuthenticator azureDevOpsAuthenticator() {
		return new AzureDevOpsAuthenticator("d6jnuqdrdy7q7jm6r5bkl4fjbwcqeyro5od2dlj2ii3thauqoz7a");
	}

	@Bean
	public AzureDevOpsClient azureDevOpsClient(AzureDevOpsAuthenticator authenticator) {
		String organizationUrl = "https://dev.azure.com/diego29122/";
		String analyticsOrganizationUrl = "https://analytics.dev.azure.com/diego29122/";
		String projectName = "ADO%20Rest/";
		return new AzureDevOpsClient(organizationUrl, authenticator, analyticsOrganizationUrl, projectName);
	}
}

//String organizationUrl = "https://dev.azure.com/diego29122/";
//String analyticsOrganizationUrl = "https://analytics.dev.azure.com/diego29122/";
//String projectName = "ADO%20Rest/";
//String personalAccessToken = "d6jnuqdrdy7q7jm6r5bkl4fjbwcqeyro5od2dlj2ii3thauqoz7a";
//String userStoryID = "3";
//String userSk = "5cfc3fc9-fb2c-4809-9586-44ffce7c24ca";
//String userEmail = "diego.29122@alunosatc.edu.br";
//int workItemID = 4;
//int remainingWork = 4;
//int completedWork = 2;
//
//AzureDevOpsAuthenticator authenticator = new AzureDevOpsAuthenticator(personalAccessToken);
//AzureDevOpsClient client = new AzureDevOpsClient(organizationUrl, authenticator, analyticsOrganizationUrl, projectName);
//
//		try {
//			String response = client.getWorItems(userStoryID, userSk);
//			System.out.println("Response: " + response);
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
//
//		try {
//			client.updateWorkItem(workItemID, UpdateWorkItemQuery(remainingWork, completedWork));
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
//
//		try {
//			String sk = client.getUserSKByEmail(userEmail);
//			System.out.println("UserSK: " + sk);
//
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}