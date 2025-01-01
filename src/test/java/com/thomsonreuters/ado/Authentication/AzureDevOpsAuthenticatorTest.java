package com.thomsonreuters.ado.Authentication;

import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.UserInformationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AzureDevOpsAuthenticatorTest {

    @Mock
    private UserInformationRepository userInformationRepository;

    @InjectMocks
    private AzureDevOpsAuthenticator azureDevOpsAuthenticator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAuthHeaderById_validUserId_returnsAuthHeader() throws Exception {
        Long userId = 1L;
        String token = "validToken";
        UserInformation userInformation = new UserInformation();
        userInformation.setToken(token);

        when(userInformationRepository.findByUserId(userId)).thenReturn(userInformation);

        String expectedAuthHeader = "Basic " + Base64.getEncoder().encodeToString((":" + token).getBytes());
        String actualAuthHeader = azureDevOpsAuthenticator.getAuthHeaderById(userId);

        assertEquals(expectedAuthHeader, actualAuthHeader);
    }

    @Test
    void getAuthHeaderById_userNotFound_throwsException() {
        Long userId = 1L;

        when(userInformationRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getAuthHeaderById(userId));
    }

    @Test
    void getAuthHeaderById_tokenNotFound_throwsException() {
        Long userId = 1L;
        UserInformation userInformation = new UserInformation();
        userInformation.setToken(null);

        when(userInformationRepository.findByUserId(userId)).thenReturn(userInformation);

        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getAuthHeaderById(userId));
    }

    @Test
    void getAuthHeaderById_tokenEmpty_throwsException() {
        Long userId = 1L;
        UserInformation userInformation = new UserInformation();
        userInformation.setToken("");
        when(userInformationRepository.findByUserId(userId)).thenReturn(userInformation);
    
        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getAuthHeaderById(userId));
    }

    @Test
    void getAuthHeaderByEmail_validEmail_returnsAuthHeader() throws Exception {
        String email = "user@example.com";
        String token = "validToken";
        UserInformation userInformation = new UserInformation();
        userInformation.setToken(token);

        when(userInformationRepository.findByEmail(email)).thenReturn(userInformation);

        String expectedAuthHeader = "Basic " + Base64.getEncoder().encodeToString((":" + token).getBytes());
        String actualAuthHeader = azureDevOpsAuthenticator.getAuthHeaderByEmail(email);

        assertEquals(expectedAuthHeader, actualAuthHeader);
    }

    @Test
    void getAuthHeaderByEmail_userNotFound_throwsException() {
        String email = "user@example.com";

        when(userInformationRepository.findByEmail(email)).thenReturn(null);

        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getAuthHeaderByEmail(email));
    }

    @Test
    void getAuthHeaderByEmail_tokenNotFound_throwsException() {
        String email = "user@example.com";
        UserInformation userInformation = new UserInformation();
        userInformation.setToken(null);

        when(userInformationRepository.findByEmail(email)).thenReturn(userInformation);

        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getAuthHeaderByEmail(email));
    }

    @Test
    void getAuthHeaderByEmail_tokenEmpty_throwsException() {
        String email = "user@example.com";
        UserInformation userInformation = new UserInformation();
        userInformation.setToken("");
        when(userInformationRepository.findByEmail(email)).thenReturn(userInformation);
    
        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getAuthHeaderByEmail(email));
    }


    @Test
    void getLocalAzureUserID_validUserId_returnsAzureUserID() throws Exception {
        Long userId = 1L;
        String azureUserID = "azureUserID";
        UserInformation userInformation = new UserInformation();
        userInformation.setAzureUserID(azureUserID);

        when(userInformationRepository.findByUserId(userId)).thenReturn(userInformation);

        String actualAzureUserID = azureDevOpsAuthenticator.getLocalAzureUserID(userId);

        assertEquals(azureUserID, actualAzureUserID);
    }

    @Test
    void getLocalAzureUserID_userNotFound_throwsException() {
        Long userId = 1L;

        when(userInformationRepository.findByUserId(userId)).thenReturn(null);

        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getLocalAzureUserID(userId));
    }

    @Test
    void getLocalAzureUserID_azureUserIDNotFound_throwsException() {
        Long userId = 1L;
        UserInformation userInformation = new UserInformation();
        userInformation.setAzureUserID(null);

        when(userInformationRepository.findByUserId(userId)).thenReturn(userInformation);

        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getLocalAzureUserID(userId));
    }

    @Test
    void getLocalAzureUserID_tokenEmpty_throwsException() {
        Long userId = 1L;
        UserInformation userInformation = new UserInformation();
        userInformation.setAzureUserID("");
        when(userInformationRepository.findByUserId(userId)).thenReturn(userInformation);
    
        assertThrows(Exception.class, () -> azureDevOpsAuthenticator.getLocalAzureUserID(userId));
    }
}