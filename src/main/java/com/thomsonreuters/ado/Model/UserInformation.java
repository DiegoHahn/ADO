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

    public UserInformation() {
    }

    public UserInformation(Long id, String email, String azureUserID, String board) {
        this.id = id;
        this.email = email;
        this.azureUserID = azureUserID;
        this.board = board;
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
}
