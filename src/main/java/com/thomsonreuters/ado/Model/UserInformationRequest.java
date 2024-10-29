package com.thomsonreuters.ado.Model;

public class UserInformationRequest {
    private String email;
    private String board;
    private String token;

    public UserInformationRequest() {}

    public UserInformationRequest(String email, String board, String token) {
        this.email = email;
        this.board = board;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
