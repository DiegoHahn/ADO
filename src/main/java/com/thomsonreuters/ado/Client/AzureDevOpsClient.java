package com.thomsonreuters.ado.Client;

import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AzureDevOpsClient {
    private final String organizationUrl;
    private final AzureDevOpsAuthenticator authenticator;
    private final HttpClient client;

    public AzureDevOpsClient(String organizationUrl, AzureDevOpsAuthenticator authenticator) {
        this.organizationUrl = organizationUrl;
        this.authenticator = authenticator;
        this.client = HttpClient.newHttpClient();
    }

    public String getWorItems(String userStoryID) throws Exception {
        String wiqlUrl = organizationUrl + "/ADO%20Rest/_apis/wit/workitems/" + userStoryID +"?api-version=7.0&$expand=relations";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(wiqlUrl))
                .header("Authorization", authenticator.getAuthHeader())
                .header("Content-Type", "application/json")
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to execute WIQL query: " + response.body());
        }

        return response.body();
    }

    public void updateWorkItem(int  workItemId, String Query) throws Exception {
        String wiqlUrl = organizationUrl + "/ADO%20Rest/_apis/wit/workitems/" + workItemId + "?api-version=7.0";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(wiqlUrl))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(Query))
                .header("Authorization", authenticator.getAuthHeader())
                .header("Content-Type", "application/json-patch+json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to execute WIQL query: " + response.body());
        }
    }
}
