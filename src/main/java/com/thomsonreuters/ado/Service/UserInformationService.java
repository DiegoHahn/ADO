package com.thomsonreuters.ado.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.UserInformationRepository;

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
