package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.UserInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class UserInformationService {
    private final UserInformationRepository userInformationRepository;

    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public UserInformation getUserInformationByUserEmail(String email) {
        return userInformationRepository.findByEmail(email);
    }

    public UserInformation saveUserInformation(UserInformation userInformation) {
        return userInformationRepository.save(userInformation);
    }
}
