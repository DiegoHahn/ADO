package com.thomsonreuters.ado.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;
import com.thomsonreuters.ado.Model.TargetWorkItem;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AzureDevOpsClient {
    private final String organizationUrl;
    private final AzureDevOpsAuthenticator authenticator;
    private final HttpClient client;
    private final String analyticsOrganizationUrl;
    private final String projectName;

    public AzureDevOpsClient(String organizationUrl, AzureDevOpsAuthenticator authenticator, String analyticsOrganizationUrl, String projectName) {
        this.organizationUrl = organizationUrl;
        this.analyticsOrganizationUrl = analyticsOrganizationUrl;
        this.authenticator = authenticator;
        this.client = HttpClient.newHttpClient();
        this.projectName = projectName;
    }

    public String getWorItems(String userStoryID, String userSK) throws Exception {
        String queryURL = analyticsOrganizationUrl + projectName
                + "_odata/v4.0-preview/WorkItems?$select=WorkItemId,AssignedToUserSK,WorkItemType"
                + "&$filter=WorkItemId%20eq%20" + userStoryID
                + "&$expand=Links($select=TargetWorkItemId;"
                + "$filter=TargetWorkItem/AssignedToUserSK%20eq%20" + userSK
                + ";$expand=TargetWorkItem($select=WorkItemId,Title,OriginalEstimate,RemainingWork,State,AssignedToUserSK))";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(queryURL))
                .header("Authorization", authenticator.getAuthHeader())
                .header("Content-Type", "application/json")
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to execute WIQL query: " + response.body());
        }

        return response.body();
    }

    public String getUserSKByEmail(String userEmail) throws Exception {
        String analyticsUrl = analyticsOrganizationUrl
                + "/_odata/v2.0/Users?$filter=UserEmail%20eq%20'"
                + userEmail
                + "'&$select=UserSK";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(analyticsUrl))
                .header("Authorization", authenticator.getAuthHeader())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to retrieve UserSK: " + response.body());
        }

        return response.body();
    }

    public void updateWorkItem(int  workItemId, String Query) throws Exception {
        String wiqlUrl = organizationUrl + projectName
                + "_apis/wit/workitems/" + workItemId
                + "?api-version=7.0";
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
