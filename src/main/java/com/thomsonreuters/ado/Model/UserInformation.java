package com.thomsonreuters.ado.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_information")
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String azureUserID;

    @Column (nullable = false)
    private String board;

    @Column (nullable = true)
    private String personalAccessToken;

    public UserInformation() {
    }

    public UserInformation(Long id, String email, String azureUserID, String board, String personalAccessToken) {
        this.id = id;
        this.email = email;
        this.azureUserID = azureUserID;
        this.board = board;
        this.personalAccessToken = personalAccessToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAzureUserID() {
        return azureUserID;
    }

    public void setAzureUserID(String azureUserID) {
        this.azureUserID = azureUserID;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
