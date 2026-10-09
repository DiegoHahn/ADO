package io.github.diegohahn.ado.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.diegohahn.ado.model.UserInformation;
import io.github.diegohahn.ado.repository.UserInformationRepository;

@Service
public class UserInformationService {
    private final UserInformationRepository userInformationRepository;

    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public UserInformation getUserInformationByUserEmail(String email) {
        return userInformationRepository.findByEmail(email);
    }

    public Optional<UserInformation> getUserInformationByUserId(Long userId) {
        return userInformationRepository.findById(userId);
    }

    public UserInformation saveUserInformation(UserInformation userInformation) {
        return userInformationRepository.save(userInformation);
    }
}
