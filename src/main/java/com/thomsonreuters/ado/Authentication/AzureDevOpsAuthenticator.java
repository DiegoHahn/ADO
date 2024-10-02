package com.thomsonreuters.ado.Authentication;

import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.UserInformationRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;

//public class AzureDevOpsAuthenticator {
//    private final String personalAccessToken;
//
//    public AzureDevOpsAuthenticator(String personalAccessToken) {
//        this.personalAccessToken = personalAccessToken;
//    }
//
//    public String getAuthHeader() {
//        return "Basic " + Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());
//    }
//}

@Service
public class AzureDevOpsAuthenticator {

    private final UserInformationRepository userInformationRepository;

    public AzureDevOpsAuthenticator(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public String getAuthHeader(String email) throws Exception {
        try {
            UserInformation userInformation = userInformationRepository.findByEmail(email);
            String personalAccessToken = userInformation.getPersonalAccessToken();
            if (personalAccessToken == null || personalAccessToken.isEmpty()) {
                throw new Exception("Personal Access Token not found for user: " + email);
            }
            return "Basic " + Base64.getEncoder().encodeToString((":" + personalAccessToken).getBytes());
        } catch (Exception e) {
            throw new Exception("User not found: " + email);
        }
    }

    public String getLocalAzureUserID(String email) throws Exception {
        try {
            UserInformation userInformation = userInformationRepository.findByEmail(email);
            String azureUserID = userInformation.getAzureUserID();
            if (azureUserID == null || azureUserID.isEmpty()) {
                throw new Exception("Azure User ID not found for user: " + email);
            }
            return azureUserID;
        } catch (Exception e) {
            throw new Exception("User not found: " + email);
        }
    }
}
