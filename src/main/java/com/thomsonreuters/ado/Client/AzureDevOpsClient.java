package com.thomsonreuters.ado.Client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.ado.Authentication.AzureDevOpsAuthenticator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Locale;

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

    public String getWorItems(String userStoryId, Long userId) throws Exception {
        String queryURL = analyticsOrganizationUrl + projectName
                + "_odata/v4.0-preview/WorkItems?$select=WorkItemId,AssignedToUserSK,WorkItemType"
                + "&$filter=WorkItemId%20eq%20" + userStoryId
                + "&$expand=Links($select=TargetWorkItemId;"
                + "$filter=TargetWorkItem/AssignedToUserSK%20eq%20" + authenticator.getLocalAzureUserID(userId)
                + ";$expand=TargetWorkItem($select=WorkItemId,Title,OriginalEstimate,RemainingWork,State,AssignedToUserSK))";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(queryURL))
                .header("Authorization", authenticator.getAuthHeaderById(userId))
                .header("Content-Type", "application/json")
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to execute WIQL query: " + response.body());
        }

        return response.body();
    }

    public String getAzureUserIDByEmail(String userEmail, String token) throws Exception {
        String analyticsUrl = analyticsOrganizationUrl
                + "/_odata/v2.0/Users?$filter=UserEmail%20eq%20'"
                + userEmail
                + "'&$select=UserSK";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(analyticsUrl))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((":" + token).getBytes()))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to retrieve AzureUserID: " + response.body());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode valueNode = rootNode.path("value");
        if (valueNode.isArray() && !valueNode.isEmpty()) {
            String userSK = valueNode.get(0).path("UserSK").asText();
            if (!userSK.isEmpty()) {
                return userSK;
            }
        }
        throw new Exception("AzureUserID not found in the response");
    }


    public void updateWorkItem(int  workItemId, String Query, Long userId) throws Exception {
        String wiqlUrl = organizationUrl + projectName
                + "_apis/wit/workitems/" + workItemId
                + "?api-version=7.0";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(wiqlUrl))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(Query))
                .header("Authorization", authenticator.getAuthHeaderById(userId))
                .header("Content-Type", "application/json-patch+json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to execute WIQL query: " + response.body());
        }
    }

    public static String UpdateWorkItemQuery(Double remainingWork, Double completedWork) {
        return String.format(Locale.US, """
            [
                {
                    "op": "add",
                    "path": "/fields/Microsoft.VSTS.Scheduling.RemainingWork",
                    "value": %.2f
                },
                {
                    "op": "add",
                    "path": "/fields/Microsoft.VSTS.Scheduling.CompletedWork",
                    "value": %.2f
                }
            ]
            """, remainingWork, completedWork);
    }
}

