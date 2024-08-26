package com.thomsonreuters.ado.Authentication;

import java.util.Base64;

public class AzureDevOpsAuthenticator {
    private final String personalAccessToken;

    public AzureDevOpsAuthenticator(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }

    public String getAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());
    }
}
