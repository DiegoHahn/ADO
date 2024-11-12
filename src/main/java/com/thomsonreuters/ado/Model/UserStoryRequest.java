package com.thomsonreuters.ado.Model;

public class UserStoryRequest {
    private String userStoryId;
    private Long userId;
    private String board;

    public UserStoryRequest(String userStoryId, Long userId, String board) {
        this.userStoryId = userStoryId;
        this.userId = userId;
        this.board = board;
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

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }
}
