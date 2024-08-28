package com.thomsonreuters.ado;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;

public class AdoApplication {

	public static void main(String[] args) {
		String organizationUrl = "https://dev.azure.com/diego29122/";
		String analyticsOrganizationUrl = "https://analytics.dev.azure.com/diego29122/";
		String projectName = "ADO%20Rest/";
		String personalAccessToken = "d6jnuqdrdy7q7jm6r5bkl4fjbwcqeyro5od2dlj2ii3thauqoz7a";
		String userStoryID = "3";
		String userSk = "5cfc3fc9-fb2c-4809-9586-44ffce7c24ca";
		String userEmail = "diego.29122@alunosatc.edu.br";
		int workItemID = 4;
		int workTimeValue = 3;
		int completedWork = 1;

		AzureDevOpsAuthenticator authenticator = new AzureDevOpsAuthenticator(personalAccessToken);
		AzureDevOpsClient client = new AzureDevOpsClient(organizationUrl, authenticator, analyticsOrganizationUrl, projectName, userSk, userEmail);

		try {
			String response = client.getWorItems(userStoryID);
			System.out.println("Response: " + response);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

//		try {
//			client.updateWorkItem(workItemID, UpdateWorkItemQuery(workTimeValue, completedWork));
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
		try {
			String sk = client.getUserSKByEmail();
			System.out.println("UserSK: " + sk);

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static String UpdateWorkItemQuery(int workTimeValue, int completedWork) {
		return String.format("""
				[
					{
						"op": "add",
						"path": "/fields/Microsoft.VSTS.Scheduling.RemainingWork",
						"value": %d
					},
					{
						"op": "add",
						"path": "/fields/Microsoft.VSTS.Scheduling.CompletedWork",
						"value": %d
					}
				]""", workTimeValue, completedWork);
	}
}
