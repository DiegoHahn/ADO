package io.github.diegohahn.ado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.diegohahn.ado.model.UserInformation;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    UserInformation findByEmail(String email);

    UserInformation findByAzureUserID(String userSK);

    UserInformation findByUserId(Long userId);
}