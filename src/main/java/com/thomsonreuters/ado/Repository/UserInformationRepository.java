package com.thomsonreuters.ado.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomsonreuters.ado.Model.UserInformation;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    UserInformation findByEmail(String email);

    UserInformation findByAzureUserID(String userSK);

    UserInformation findByUserId(Long userId);
}