package com.thomsonreuters.ado.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_information")
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String azureUserID;

    @Column
    private String board;

    @Column (nullable = true)
    private String personalAccessToken;

    @OneToMany(mappedBy = "userId")
    private List<ActivityRecord> activityRecords;

    public UserInformation() {
    }

    public UserInformation(Long userId, String email, String azureUserID, String board, String personalAccessToken) {
        this.userId = userId;
        this.email = email;
        this.azureUserID = azureUserID;
        this.board = board;
        this.personalAccessToken = personalAccessToken;
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
