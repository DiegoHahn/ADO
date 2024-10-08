package com.thomsonreuters.ado.Model;

public class UserStoryRequest {
    private String userStoryId;
    private String userEmail;

    public UserStoryRequest(String userStoryId, String userEmail) {
        this.userStoryId = userStoryId;
        this.userEmail = userEmail;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
