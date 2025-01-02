package com.thomsonreuters.ado.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Exceptions.InvalidTokenException;
import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Model.ActivityRecordResponseDTO;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Service.ActivityRecordService;
import com.thomsonreuters.ado.Service.UserInformationService;



@ExtendWith(MockitoExtension.class)
class ActivityRecordControllerTest {

    @Mock
    private ActivityRecordService activityRecordService;
    @Mock
    private UserInformationService userInformationService;
    @Mock
    private AzureDevOpsClient azureDevOpsClient;

    @InjectMocks
    private ActivityRecordController activityRecordController;

    @Test
    void testCreateActivityRecord_Success() throws InvalidTokenException, Exception {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(1L);
        ActivityRecord mockRecord = new ActivityRecord();
        when(activityRecordService.saveActivityRecord(dto)).thenReturn(mockRecord);

        UserInformation user = new UserInformation();
        user.setEmail("test@example.com");
        user.setToken("validToken");
        when(userInformationService.getUserInformationByUserId(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = activityRecordController.createActivityRecord(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(activityRecordService).saveActivityRecord(dto);
        verify(azureDevOpsClient).getAzureUserIDByEmail("test@example.com", "validToken");
    }

    @SuppressWarnings("null")
    @Test
    void testCreateActivityRecord_UserNotFound() throws UserNotFoundException {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(99L);
        when(activityRecordService.saveActivityRecord(dto)).thenReturn(new ActivityRecord());
        when(userInformationService.getUserInformationByUserId(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = activityRecordController.createActivityRecord(dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Usuário não encontrado"));
    }

    @SuppressWarnings("null")
    @Test
    void testCreateActivityRecord_NullToken() throws UserNotFoundException {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(1L);
        when(activityRecordService.saveActivityRecord(dto)).thenReturn(new ActivityRecord());
        UserInformation user = new UserInformation();
        user.setEmail("test@example.com");
        user.setToken(null);
        when(userInformationService.getUserInformationByUserId(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = activityRecordController.createActivityRecord(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Token necessário"));
    }

    @SuppressWarnings("null")
    @Test
    void testCreateActivityRecord_InvalidToken() throws InvalidTokenException, Exception {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(1L);
        when(activityRecordService.saveActivityRecord(dto)).thenReturn(new ActivityRecord());
        UserInformation user = new UserInformation();
        user.setEmail("test@example.com");
        user.setToken("invalidToken");
        when(userInformationService.getUserInformationByUserId(1L)).thenReturn(Optional.of(user));
        doThrow(new InvalidTokenException("Invalid")).when(azureDevOpsClient)
                .getAzureUserIDByEmail("test@example.com", "invalidToken");

        ResponseEntity<?> response = activityRecordController.createActivityRecord(dto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Token inválido"));
    }

    @SuppressWarnings("null")
    @Test
    void testCreateActivityRecord_UserNotFoundByEmail() throws InvalidTokenException, Exception {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(1L);
        when(activityRecordService.saveActivityRecord(dto)).thenReturn(new ActivityRecord());
        UserInformation user = new UserInformation();
        user.setEmail("unknown@example.com");
        user.setToken("validToken");
        when(userInformationService.getUserInformationByUserId(1L)).thenReturn(Optional.of(user));
        doThrow(new UserNotFoundException("Not found")).when(azureDevOpsClient)
                .getAzureUserIDByEmail("unknown@example.com", "validToken");

        ResponseEntity<?> response = activityRecordController.createActivityRecord(dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Usuário não encontrado para o email"));
    }

    @SuppressWarnings("null")
    @Test
    void testCreateActivityRecord_InternalServerError() throws Exception {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(1L);
        when(activityRecordService.saveActivityRecord(dto)).thenThrow(new RuntimeException("DB issue"));
    
        ResponseEntity<?> response = activityRecordController.createActivityRecord(dto);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());        
        assertTrue(response.getBody().toString().contains("Erro ao processar o registro de atividade."));
    }

    @Test
    void testGetActivityRecordsByDate_Success() {
        Long userId = 1L;
        String date = "2023-09-10";
        
        List<ActivityRecordResponseDTO> mockRecords = List.of(
            new ActivityRecordResponseDTO(),
            new ActivityRecordResponseDTO()
        );
        
        when(activityRecordService.getActivityRecordsByDate(userId, date)).thenReturn(mockRecords);
        
        ResponseEntity<List<ActivityRecordResponseDTO>> response = 
                activityRecordController.getActivityRecordsByDate(userId, date);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecords, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void testGetActivityRecordsByDate_EmptyResult() {
        Long userId = 2L;
        String date = "2023-09-11";
        when(activityRecordService.getActivityRecordsByDate(userId, date)).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<ActivityRecordResponseDTO>> response = 
                activityRecordController.getActivityRecordsByDate(userId, date);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetActivityRecordsByWorkItemId_Success() {
        Long userId = 1L;
        int workItemId = 101;
        List<ActivityRecordResponseDTO> mockRecords = List.of(
            new ActivityRecordResponseDTO(),
            new ActivityRecordResponseDTO()
        );
        when(activityRecordService.getActivityRecordsByWorkItemID(userId, workItemId)).thenReturn(mockRecords);
        
        ResponseEntity<List<ActivityRecordResponseDTO>> response = 
                activityRecordController.getActivityRecordsByWorkItemID(userId, workItemId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecords, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void testGetActivityRecordsByWorkItemId_EmptyResult() {
        Long userId = 2L;
        int workItemId = 202;
        when(activityRecordService.getActivityRecordsByWorkItemID(userId, workItemId)).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<ActivityRecordResponseDTO>> response = 
                activityRecordController.getActivityRecordsByWorkItemID(userId, workItemId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}