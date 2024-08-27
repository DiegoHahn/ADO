package com.thomsonreuters.ado;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;

public class AdoApplication {

	public static void main(String[] args) {
		String organizationUrl = "https://dev.azure.com/diego29122";
		String personalAccessToken = "d6jnuqdrdy7q7jm6r5bkl4fjbwcqeyro5od2dlj2ii3thauqoz7a";
		int userStoryID = 3;
		int workItemID = 4;
		int workTimeValue = 5;

		AzureDevOpsAuthenticator authenticator = new AzureDevOpsAuthenticator(personalAccessToken);
		AzureDevOpsClient client = new AzureDevOpsClient(organizationUrl, authenticator);

		//Cria uma string com a query WIQL para fazer a requisição
		String query = createWiqlQuery(userStoryID);

		try {
			String response = client.runWiqlQuery(query);
			System.out.println("Response: " + response);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		try {
			String updateResponse = client.updateWorkItem(workItemID, createUpdateWorkItem(workTimeValue));
			System.out.println("Update Response: " + updateResponse);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static String createWiqlQuery(int userStoryID) {
		return String.format("""
                {"query": "SELECT
                    [System.Id]
                FROM workitemLinks
                WHERE
                    (
                        [Source].[System.AssignedTo] = @me
                        AND [Source].[System.State] <> 'Closed'
                        AND [Source].[System.Parent] = %d
                        AND [Source].[System.WorkItemType] = 'Task'
                    )
                    AND (
                        [System.Links.LinkType] = 'System.LinkTypes.Hierarchy-Forward'
                    )
                ORDER BY [System.Id]
                MODE (Recursive)"}""", userStoryID);
	}

	public static String createUpdateWorkItem(int workTimeValue) {
		return String.format("""
				[
					{
						"op": "add",
						"path": "/fields/Microsoft.VSTS.Scheduling.RemainingWork",
						"value": %d
					}
				]""", workTimeValue);
	}
}
