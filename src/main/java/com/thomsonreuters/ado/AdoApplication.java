package com.thomsonreuters.ado;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdoApplication {

	public static void main(String[] args) {
		String organizationUrl = "https://dev.azure.com/diego29122";
		String personalAccessToken = "d6jnuqdrdy7q7jm6r5bkl4fjbwcqeyro5od2dlj2ii3thauqoz7a";
		String userStoryID = "3";
		int workItemID = 4;
		int workTimeValue = 3;
		int completedWork = 1;

		AzureDevOpsAuthenticator authenticator = new AzureDevOpsAuthenticator(personalAccessToken);
		AzureDevOpsClient client = new AzureDevOpsClient(organizationUrl, authenticator);

		try {
			String response = client.getWorItems(userStoryID);
			System.out.println("Response: " + response);
			List<String> relationUrls = getRelationUrls(response);
			System.out.println("URLs das Work Items: " + relationUrls);

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}


		try {
			client.updateWorkItem(workItemID, UpdateWorkItemQuery(workTimeValue, completedWork));
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}


	public static List<String> getRelationUrls(String jsonResponse) throws JSONException {
		List<String> urls = new ArrayList<>();

		// Converte a resposta em um objeto JSON
		JSONObject jsonObject = new JSONObject(jsonResponse);

		// Cria um array com apenas as relações do objeto JSON
		JSONArray relations = jsonObject.optJSONArray("relations");

		// Adiciona a URL à lista
		if (relations != null) {
			for (int i = 0; i < relations.length(); i++) {
				JSONObject relation = relations.getJSONObject(i);
				String url = relation.optString("url");
				if (!url.isEmpty()) {
					urls.add(url);
				}
			}
		}
		return urls;
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
