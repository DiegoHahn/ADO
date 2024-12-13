package com.thomsonreuters.ado.Model;

public class UserStoryRequest {
    private String userStoryId;
    private Long userId;
    private String board;
    private boolean concluded;

    public UserStoryRequest(String userStoryId, Long userId, String board, boolean concluded) {
        this.userStoryId = userStoryId;
        this.userId = userId;
        this.board = board;
        this.concluded = concluded;
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

    public boolean isConcluded() {
        return concluded;
    }

    public void setConcluded(boolean concluded) {
        this.concluded = concluded;
    }
}
