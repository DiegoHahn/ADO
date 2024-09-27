package com.thomsonreuters.ado.Repository;

import com.thomsonreuters.ado.Model.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    UserInformation findByEmail(String email);

    UserInformation findByAzureUserID(String userSK);
}