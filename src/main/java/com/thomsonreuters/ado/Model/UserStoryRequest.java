package com.thomsonreuters.ado.Model;

public class UserStoryRequest {
    private String userStoryId;
    private Long userId;

    public UserStoryRequest(String userStoryId, Long userId) {
        this.userStoryId = userStoryId;
        this.userId = userId;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
