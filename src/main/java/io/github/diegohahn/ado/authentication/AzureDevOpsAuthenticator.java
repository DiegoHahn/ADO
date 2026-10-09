package io.github.diegohahn.ado.authentication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

import io.github.diegohahn.ado.exceptions.InvalidTokenException;
import io.github.diegohahn.ado.exceptions.UserNotFoundException;
import io.github.diegohahn.ado.model.UserInformation;
import io.github.diegohahn.ado.repository.UserInformationRepository;

@Service
public class AzureDevOpsAuthenticator {

    private final UserInformationRepository userInformationRepository;

    public AzureDevOpsAuthenticator(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public String getAuthHeaderById(Long userId) throws UserNotFoundException, InvalidTokenException {
        UserInformation userInformation = userInformationRepository.findByUserId(userId);
        if (userInformation == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        return buildAuthHeader(userInformation.getToken(), String.valueOf(userId));
    }

    public String getAuthHeaderByEmail(String email) throws UserNotFoundException, InvalidTokenException {
        UserInformation userInformation = userInformationRepository.findByEmail(email);
        if (userInformation == null) {
            throw new UserNotFoundException("User not found: " + email);
        }
        return buildAuthHeader(userInformation.getToken(), email);
    }

    public String getLocalAzureUserID(Long userId) throws UserNotFoundException {
        UserInformation userInformation = userInformationRepository.findByUserId(userId);
        if (userInformation == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        String azureUserID = userInformation.getAzureUserID();
        if (azureUserID == null || azureUserID.isEmpty()) {
            throw new UserNotFoundException("Azure DevOps user ID not stored for user: " + userId);
        }
        return azureUserID;
    }

    private static String buildAuthHeader(String personalAccessToken, String user) throws InvalidTokenException {
        if (personalAccessToken == null || personalAccessToken.isEmpty()) {
            throw new InvalidTokenException("No Personal Access Token stored for user: " + user);
        }
        return "Basic " + Base64.getEncoder()
                .encodeToString((":" + personalAccessToken).getBytes(StandardCharsets.UTF_8));
    }
}
