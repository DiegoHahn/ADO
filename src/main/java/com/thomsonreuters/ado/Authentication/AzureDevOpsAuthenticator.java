package com.thomsonreuters.ado.Authentication;

import java.util.Base64;

import org.springframework.stereotype.Service;

import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.UserInformationRepository;

@Service
public class AzureDevOpsAuthenticator {

    private final UserInformationRepository userInformationRepository;

    public AzureDevOpsAuthenticator(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public String getAuthHeaderById(Long userId) throws Exception {
        try {
            UserInformation userInformation = userInformationRepository.findByUserId(userId);
            String personalAccessToken = userInformation.getToken();
            if (personalAccessToken == null || personalAccessToken.isEmpty()) {
                throw new Exception("Personal Access Token not found for user: " + userId);
            }
            return "Basic " + Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());
        } catch (Exception e) {
            throw new Exception("User not found: " + userId);
        }
    }

    public String getAuthHeaderByEmail(String email) throws Exception {
        try {
            UserInformation userInformation = userInformationRepository.findByEmail(email);
            String personalAccessToken = userInformation.getToken();
            if (personalAccessToken == null || personalAccessToken.isEmpty()) {
                throw new Exception("Personal Access Token not found for user: " + email);
            }
            return "Basic " + Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());
        } catch (Exception e) {
            throw new Exception("User not found: " + email);
        }
    }

    public String getLocalAzureUserID(Long userId) throws Exception {
        try {
            UserInformation userInformation = userInformationRepository.findByUserId(userId);
            String azureUserID = userInformation.getAzureUserID();
            if (azureUserID == null || azureUserID.isEmpty()) {
                throw new Exception("Azure User ID not found for user: " + userId);
            }
            return azureUserID;
        } catch (Exception e) {
            throw new Exception("User not found: " + userId);
        }
    }
}
