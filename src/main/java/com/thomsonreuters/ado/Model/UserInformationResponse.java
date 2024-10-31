package com.thomsonreuters.ado.Model;

public class UserInformationResponse {
    private Long userId;
    private String email;
    private String board;
    private String azureUserID;
    private boolean hasToken;

    public UserInformationResponse(Long userId, String email, String board, String azureUserID, boolean hasToken) {
        this.userId = userId;
        this.email = email;
        this.board = board;
        this.azureUserID = azureUserID;
        this.hasToken = hasToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getAzureUserID() {
        return azureUserID;
    }

    public void setAzureUserID(String azureUserID) {
        this.azureUserID = azureUserID;
    }

    public boolean isHasToken() {
        return hasToken;
    }

    public void setHasToken(boolean hasToken) {
        this.hasToken = hasToken;
    }
}
