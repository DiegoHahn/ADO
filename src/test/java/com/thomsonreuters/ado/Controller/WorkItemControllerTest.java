package com.thomsonreuters.ado.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Model.UserStoryRequest;
import com.thomsonreuters.ado.Service.WorkItemService;


class WorkItemControllerTest {

    @Mock
    private WorkItemService workItemService;

    @Mock
    private AzureDevOpsClient azureDevOpsClient;

    @InjectMocks
    private WorkItemController workItemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

       @Test
        void getTargetWorkItemsForUserStory_Success() throws Exception {
            UserStoryRequest request = new UserStoryRequest(null, null, null, false);
            request.setUserStoryId("US123");
            request.setUserId(1L);
            request.setBoard("Board1");

            String azureResponse = "{\"data\":\"sample\"}";
            List<TargetWorkItem> expectedWorkItems = Arrays.asList(
                    new TargetWorkItem(0, azureResponse, azureResponse, null, null, null));

            when(azureDevOpsClient.getWorItems("US123", 1L, "Board1")).thenReturn(azureResponse);
            when(workItemService.processAzureDevOpsResponse(azureResponse, request)).thenReturn(expectedWorkItems);

            ResponseEntity<List<TargetWorkItem>> response = workItemController.getTargetWorkItemsForUserStory(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedWorkItems, response.getBody());

            verify(azureDevOpsClient, times(1)).getWorItems("US123", 1L, "Board1");
            verify(workItemService, times(1)).processAzureDevOpsResponse(azureResponse, request);
        }

    @Test
    void getTargetWorkItemsForUserStory_JsonProcessingException() throws Exception {
        UserStoryRequest request = new UserStoryRequest(null, null, null, false);
        request.setUserStoryId("US123");
        request.setUserId(1L);
        request.setBoard("Board1");

        String azureResponse = "{\"data\":\"sample\"}";

        when(azureDevOpsClient.getWorItems("US123", 1L, "Board1")).thenReturn(azureResponse);

        JsonProcessingException mockException = mock(JsonProcessingException.class);
        when(mockException.toString()).thenReturn("com.fasterxml.jackson.core.JsonProcessingException: Error");

        when(workItemService.processAzureDevOpsResponse(azureResponse, request))
                .thenThrow(mockException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            workItemController.getTargetWorkItemsForUserStory(request);
        });

        assertEquals("com.fasterxml.jackson.core.JsonProcessingException: Error", exception.getMessage());
        verify(azureDevOpsClient, times(1)).getWorItems("US123", 1L, "Board1");
        verify(workItemService, times(1)).processAzureDevOpsResponse(azureResponse, request);
    }
}