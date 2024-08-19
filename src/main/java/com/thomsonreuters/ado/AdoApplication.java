package com.thomsonreuters.ado;

import com.microsoft.azure.devops.AzureDevOpsClient;
import com.microsoft.azure.devops.models.WorkItem;
import java.util.List;

public class AzureDevOpsExample {

	public static void main(String[] args) {
		String organizationUrl = "https://dev.azure.com/your-organization";
		String personalAccessToken = "your-personal-access-token";

		AzureDevOpsClient client = new AzureDevOpsClient(organizationUrl, personalAccessToken);

		// Executa a consulta para trazer os Work Items atribuídos a você
		String wiqlQuery = "SELECT [System.Id], [System.Title] FROM WorkItems WHERE [System.AssignedTo] = @Me";
		List<WorkItem> workItems = client.getWorkItems(wiqlQuery);

		for (WorkItem workItem : workItems) {
			System.out.println("ID: " + workItem.getId() + ", Title: " + workItem.getFields().get("System.Title"));
		}
	}
}
