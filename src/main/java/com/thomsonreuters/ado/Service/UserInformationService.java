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

    public UserInformation getUserInformationByUserSK(String userSK) {
        return userInformationRepository.findByUserSK(userSK);
    }

    public UserInformation saveUserInformation(UserInformation userInformation) {
        return userInformationRepository.save(userInformation);
    }
}
