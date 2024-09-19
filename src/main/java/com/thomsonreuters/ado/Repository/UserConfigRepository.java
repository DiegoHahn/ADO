package com.thomsonreuters.ado.Repository;

import com.thomsonreuters.ado.Model.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
    UserConfig findByEmail(String email);
}