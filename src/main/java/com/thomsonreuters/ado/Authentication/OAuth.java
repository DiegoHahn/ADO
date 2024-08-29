package com.thomsonreuters.ado.Authentication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OAuth {
    private final String clientId;
//    private final String clientSecret;
    private final String redirectUri;
//    private final String redirectUri2 = "http://localhost:8080";
    private final String authorizationEndpoint;

    public OAuth(String clientId, String redirectUri, String authorizationEndpoint) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.authorizationEndpoint = authorizationEndpoint;
    }
//    private final String tokenEndpoint;
//
//    public OAuth(String clientId, String clientSecret, String redirectUri, String authorizationEndpoint, String tokenEndpoint) {
//        this.clientId = clientId;
//        this.clientSecret = clientSecret;
//        this.redirectUri = redirectUri;
//        this.authorizationEndpoint = authorizationEndpoint;
//        this.tokenEndpoint = tokenEndpoint;
//    }

    public String getAuthorizationUrl(String state, String scope) {
        return authorizationEndpoint + "?client_id=" + clientId +
                "&response_type=code&redirect_uri=" + redirectUri +
                "&scope=" + scope + "&state=" + state;
    }
//
//    public String getAccessToken(String authorizationCode) throws Exception {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(tokenEndpoint))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .POST(HttpRequest.BodyPublishers.ofString(
//                        "client_id=" + clientId +
//                                "&grant_type=authorization_code" +
//                                "&code=" + authorizationCode +
//                                "&redirect_uri=" + redirectUri +
//                                "&client_secret=" + clientSecret))
//                .build();
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        // Parse the response to extract the access token
//        return parseAccessToken(response.body());
//    }
//
//    private String parseAccessToken(String responseBody) {
//
//        return "";
//    }
}