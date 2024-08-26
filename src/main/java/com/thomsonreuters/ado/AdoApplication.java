package com.thomsonreuters.ado;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;

public class AdoApplication {

	public static void main(String[] args) {
		String organizationUrl = "https://dev.azure.com/diego29122";
		String personalAccessToken = "d6jnuqdrdy7q7jm6r5bkl4fjbwcqeyro5od2dlj2ii3thauqoz7a";

		AzureDevOpsAuthenticator authenticator = new AzureDevOpsAuthenticator(personalAccessToken);
		AzureDevOpsClient client = new AzureDevOpsClient(organizationUrl, authenticator);

		String wiqlQuery = "{\"query\": \"SELECT [System.Id], [System.Title] FROM WorkItems WHERE [System.AssignedTo] = @Me\"}";

		try {
			String response = client.runWiqlQuery(wiqlQuery);
			System.out.println("Response: " + response);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
