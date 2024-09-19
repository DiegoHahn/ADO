package com.thomsonreuters.ado.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")


public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String userSK;

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

    public String getUserSK() {
        return userSK;
    }

    public void setUserSK(String userSK) {
        this.userSK = userSK;
    }
}
