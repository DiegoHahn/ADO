package com.thomsonreuters.ado.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Exceptions.InvalidTokenException;
import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.AzureUserIDRequest;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Model.UserInformationRequest;
import com.thomsonreuters.ado.Model.UserInformationResponse;
import com.thomsonreuters.ado.Service.ActivityRecordService;
import com.thomsonreuters.ado.Service.UserInformationService;

class UserInformationControllerTest {

    @Mock
    private AzureDevOpsClient azureDevOpsClient;

    @Mock
    private UserInformationService userInformationService;

    @Mock
    private ActivityRecordService activityRecordService;

    @InjectMocks
    private UserInformationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCurrentUserInformationSuccess() {
        String email = "user@example.com";
        UserInformation mockUser = new UserInformation();
        mockUser.setUserId(1L);
        mockUser.setEmail(email);
        when(userInformationService.getUserInformationByUserEmail(email)).thenReturn(mockUser);

        AzureUserIDRequest request = new AzureUserIDRequest();
        request.setEmail(email);
        ResponseEntity<UserInformationResponse> response = controller.getCurrentUserInformation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userInformationService, times(1)).getUserInformationByUserEmail(email);
    }
    
    @SuppressWarnings("null")
    @Test
    void testGetCurrentUserInformationHasToken() {
        // Arrange
        String email = "hastoken@example.com";
        UserInformation mockUser = new UserInformation();
        mockUser.setUserId(1L);
        mockUser.setEmail(email);
        mockUser.setToken("validToken123");
        when(userInformationService.getUserInformationByUserEmail(email)).thenReturn(mockUser);
    
        AzureUserIDRequest request = new AzureUserIDRequest();
        request.setEmail(email);
    
        // Act
        ResponseEntity<UserInformationResponse> response = controller.getCurrentUserInformation(request);
    
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().isHasToken());
    }

    @Test
    void testGetCurrentUserInformationNotFound() {
        String email = "notfound@example.com";
        when(userInformationService.getUserInformationByUserEmail(email)).thenReturn(null);
        AzureUserIDRequest request = new AzureUserIDRequest();
        request.setEmail(email);

        ResponseEntity<UserInformationResponse> response = controller.getCurrentUserInformation(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userInformationService, times(1)).getUserInformationByUserEmail(email);
    }

    @Test
    void testGetCurrentUserInformationDataAccessError() {
        String email = "dberror@example.com";
        when(userInformationService.getUserInformationByUserEmail(email))
                .thenThrow(new DataAccessException("DB error") {});

        AzureUserIDRequest request = new AzureUserIDRequest();
        request.setEmail(email);
        ResponseEntity<UserInformationResponse> response = controller.getCurrentUserInformation(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userInformationService, times(1)).getUserInformationByUserEmail(email);
    }

    @SuppressWarnings("null")
    @Test
    void testGetCurrentUserInformationNullToken() {
        String email = "nulltoken@example.com";
        UserInformation mockUser = new UserInformation();
        mockUser.setUserId(1L);
        mockUser.setEmail(email);
        mockUser.setToken(null);
        when(userInformationService.getUserInformationByUserEmail(email)).thenReturn(mockUser);

        AzureUserIDRequest request = new AzureUserIDRequest();
        request.setEmail(email);

        ResponseEntity<UserInformationResponse> response = controller.getCurrentUserInformation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().isHasToken());
    }

    @Test
    void testSaveOrUpdateUserInformationCreateNewUserSuccess() throws Exception {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("new@example.com");
        request.setBoard("NewBoard");
        request.setToken("validToken");
        when(userInformationService.getUserInformationByUserEmail(request.getEmail())).thenReturn(null);
        when(azureDevOpsClient.getAzureUserIDByEmail(anyString(), anyString())).thenReturn("azureID");

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Novo usuário criado e azureUserID salvo com sucesso!", response.getBody());
        verify(userInformationService, times(1)).saveUserInformation(any(UserInformation.class));
    }

    @Test
    void testSaveOrUpdateUserInformation_NoTokenAndNoExistingUser() {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("notfound@example.com");
        request.setBoard("BoardWithoutToken");
        request.setToken(null); 
        when(userInformationService.getUserInformationByUserEmail(request.getEmail())).thenReturn(null);

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token necessário para criação de novo registro.", response.getBody());
    }

    @Test
    void testSaveOrUpdateUserInformationUpdateUserSuccess() throws Exception {
        UserInformation existingUser = new UserInformation();
        existingUser.setEmail("existing@example.com");
        existingUser.setUserId(2L);

        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("existing@example.com");
        request.setBoard("UpdatedBoard");
        request.setToken("validToken");

        when(userInformationService.getUserInformationByUserEmail(request.getEmail())).thenReturn(existingUser);

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dados do usuário atualizados com sucesso!", response.getBody());
        verify(userInformationService, times(1)).saveUserInformation(existingUser);
        verify(activityRecordService, times(1))
                .updateActivityRecordsStatus(existingUser.getUserId(), 2, 1);
    }

    @Test
    void testSaveOrUpdateUserInformationInvalidToken() throws Exception {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("tokenfail@example.com");
        request.setToken("invalidToken");

        when(userInformationService.getUserInformationByUserEmail(request.getEmail())).thenReturn(null);
        doThrow(new InvalidTokenException("Invalid token")).when(azureDevOpsClient)
                .getAzureUserIDByEmail(anyString(), anyString());

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token inválido ou expirado.", response.getBody());
    }

    @Test
    void testSaveOrUpdateUserInformationUserNotFound() throws Exception {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("notfound@example.com");
        request.setToken("validToken");

        when(userInformationService.getUserInformationByUserEmail(request.getEmail())).thenReturn(null);
        doThrow(new UserNotFoundException("Not found")).when(azureDevOpsClient)
                .getAzureUserIDByEmail(anyString(), anyString());

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuário não encontrado para o email fornecido.", response.getBody());
    }

    @Test
    void testSaveOrUpdateUserInformationMissingToken() {
        UserInformation existingUser = null;
        String email = "missingtoken@example.com";
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail(email);

        when(userInformationService.getUserInformationByUserEmail(email)).thenReturn(existingUser);
        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token necessário para criação de novo registro.", response.getBody());
    }

    @Test
    void testSaveOrUpdateUserInformation_NoRequestTokenButExistingHasToken() throws Exception {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("existing@example.com");
        request.setBoard("BoardWithoutToken");
        request.setToken(null);
        UserInformation existingUser = new UserInformation();
        existingUser.setEmail("existing@example.com");
        existingUser.setToken("existingToken");

        when(userInformationService.getUserInformationByUserEmail(request.getEmail()))
                .thenReturn(existingUser);

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userInformationService).saveUserInformation(existingUser);
    }

    @Test
    void testSaveOrUpdateUserInformation_GenericException() {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("genericException@example.com");
        request.setToken("anyToken");
        when(userInformationService.getUserInformationByUserEmail(request.getEmail()))
                .thenThrow(new RuntimeException("Generic Error"));

        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro ao processar os dados do usuário.", response.getBody());
    }

    @Test
    void testSaveOrUpdateUserInformation_TokenIsEmptyString() {
        UserInformationRequest request = new UserInformationRequest();
        request.setEmail("empty@example.com");
        request.setToken("");
        when(userInformationService.getUserInformationByUserEmail(request.getEmail()))
                .thenReturn(null);
        ResponseEntity<String> response = controller.saveOrUpdateUserInformation(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token necessário para criação de novo registro.", response.getBody());
    }
}