package com.thomsonreuters.ado.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;

@ExtendWith(MockitoExtension.class)
class ScheduledUpdateServiceTest {
    
    @Mock
    private ActivityRecordRepository activityRecordRepository;

    @Mock
    private AzureDevOpsClient azureDevOpsClient;

    @Mock
    private WorkItemService workItemService;

    @InjectMocks
    private ScheduledUpdateService scheduledUpdateService;

    @Test
    void updatePendingWorkItems_updatesWorkItemsSuccessfully() {
        ActivityRecord record = new ActivityRecord();
        record.setStatus(1);
        List<ActivityRecord> records = List.of(record);

        when(activityRecordRepository.findTop20ByStatus(1)).thenReturn(records);

        scheduledUpdateService.updatePendingWorkItems();

        verify(activityRecordRepository, times(1)).findTop20ByStatus(1);
        verify(activityRecordRepository, times(1)).save(record);
    }

    @Test
    void updatePendingWorkItems_handlesEmptyPendingRecords() {
        when(activityRecordRepository.findTop20ByStatus(1)).thenReturn(List.of());

        scheduledUpdateService.updatePendingWorkItems();

        verify(activityRecordRepository, times(1)).findTop20ByStatus(1);
        verify(activityRecordRepository, never()).save(any(ActivityRecord.class));
    }

    @Test
    void updateWorkItem_updatesWorkItemSuccessfully() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("1:00:00");
        record.setState("Active");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem targetWorkItem = new TargetWorkItem();
        targetWorkItem.setWorkItemId(1);
        targetWorkItem.setCompletedWork(0.0);
        targetWorkItem.setRemainingWork(5.0);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(targetWorkItem));

        double expectedRemaining = 4.0; 
        double expectedCompleted = 1.0; 
        String expectedQuery = AzureDevOpsClient.UpdateWorkItemQueryCompletedAndRemaining(expectedRemaining, expectedCompleted);

        doNothing().when(azureDevOpsClient)
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1))
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(0, record.getStatus(), "O status do record deveria ser 0 após a atualização bem-sucedida.");
    }

    @Test
    void updateWorkItem_handlesMultipleTargetWorkItems() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("1:00:00");
        record.setState("Active");
        record.setStatus(1); 

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem matchingWorkItem = new TargetWorkItem();
        matchingWorkItem.setWorkItemId(1);
        matchingWorkItem.setCompletedWork(0.0);
        matchingWorkItem.setRemainingWork(5.0);

        TargetWorkItem nonMatchingWorkItem = new TargetWorkItem();
        nonMatchingWorkItem.setWorkItemId(2); // ID diferente
        nonMatchingWorkItem.setCompletedWork(3.0);
        nonMatchingWorkItem.setRemainingWork(2.0);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(matchingWorkItem, nonMatchingWorkItem));

        double expectedCompleted = 1.0;
        double expectedRemaining = 4.0;
        String expectedQuery = AzureDevOpsClient.UpdateWorkItemQueryCompletedAndRemaining(expectedRemaining, expectedCompleted);

        doNothing().when(azureDevOpsClient)
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1))
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(0, record.getStatus(), "O status do record deveria ser 0 após a atualização bem-sucedida.");
    }

    @Test
    void updateWorkItem_handlesWorkItemNotFound() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("1:00:00");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse(eq("response")))
            .thenReturn(List.of());

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, never())
            .updateWorkItem(anyInt(), anyString(), eq(1L), anyString());
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(2, record.getStatus(), "O status do record deveria ser 2 após falha na atualização.");
    }

    @Test
    void updateWorkItem_handlesNoMatchingTargetWorkItems() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("1:00:00");
        record.setState("Active");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem nonMatchingWorkItem1 = new TargetWorkItem();
        nonMatchingWorkItem1.setWorkItemId(2);
        nonMatchingWorkItem1.setCompletedWork(3.0);
        nonMatchingWorkItem1.setRemainingWork(2.0);

        TargetWorkItem nonMatchingWorkItem2 = new TargetWorkItem();
        nonMatchingWorkItem2.setWorkItemId(3);
        nonMatchingWorkItem2.setCompletedWork(4.0);
        nonMatchingWorkItem2.setRemainingWork(1.0);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(nonMatchingWorkItem1, nonMatchingWorkItem2));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, never())
            .updateWorkItem(anyInt(), anyString(), eq(1L), anyString());

        verify(activityRecordRepository, times(1))
            .save(record);

        assertEquals(2, record.getStatus(), "O status do record deveria ser 2 após falha na atualização.");
    }

    @Test
    void updateWorkItem_handlesClosedStateSuccessfully() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("2:30:00");
        record.setState("Closed");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem targetWorkItem = new TargetWorkItem();
        targetWorkItem.setWorkItemId(1);
        targetWorkItem.setCompletedWork(1.0);
        targetWorkItem.setRemainingWork(4.0);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(targetWorkItem));

        double expectedCompleted = 1.0 + 2.5;
        String expectedQuery = AzureDevOpsClient.UpdateWorkItemQueryCompleted(expectedCompleted);

        doNothing().when(azureDevOpsClient)
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1))
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(0, record.getStatus(), "O status do record deveria ser 0 após a atualização bem-sucedida.");
    }

    @Test
    void updateWorkItem_handlesActiveStateWithNullRemainingWork() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("1:30:00");
        record.setState("Active");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem targetWorkItem = new TargetWorkItem();
        targetWorkItem.setWorkItemId(1);
        targetWorkItem.setCompletedWork(2.0);
        targetWorkItem.setRemainingWork(null);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(targetWorkItem));

        double expectedCompleted = 2.0 + 1.5; // 3.5
        String expectedQuery = AzureDevOpsClient.UpdateWorkItemQueryCompleted(expectedCompleted);

        doNothing().when(azureDevOpsClient)
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1))
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(0, record.getStatus(), "O status do record deveria ser 0 após a atualização bem-sucedida.");
    }

    @Test
    void updateWorkItem_handlesActiveStateWithRemainingWork() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("0:30:00");
        record.setState("Active");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem targetWorkItem = new TargetWorkItem();
        targetWorkItem.setWorkItemId(1);
        targetWorkItem.setCompletedWork(1.0);
        targetWorkItem.setRemainingWork(3.0);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(targetWorkItem));

        double expectedCompleted = 1.0 + 0.5;
        double expectedRemaining = 3.0 - 0.5;
        String expectedQuery = AzureDevOpsClient.UpdateWorkItemQueryCompletedAndRemaining(expectedRemaining, expectedCompleted);

        doNothing().when(azureDevOpsClient)
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1))
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(0, record.getStatus(), "O status do record deveria ser 0 após a atualização bem-sucedida.");
    }
    
    @Test
    void updateWorkItem_handlesActiveStateWithRemainingWorkNull() throws Exception {
        ActivityRecord record = new ActivityRecord();
        record.setWorkItemId(1);
        record.setCurrentTrackedTime("1:00:00");
        record.setState("Active");
        record.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(1L);
        record.setUserId(user);
        record.setUserStoryId("US123");
        record.setBoard("Board1");

        TargetWorkItem targetWorkItem = new TargetWorkItem();
        targetWorkItem.setWorkItemId(1);
        targetWorkItem.setCompletedWork(2.0);
        targetWorkItem.setRemainingWork(null);
        targetWorkItem.setOriginalEstimate(5.0);

        when(azureDevOpsClient.getWorItems(eq("US123"), eq(1L), eq("Board1")))
            .thenReturn("response");

        when(workItemService.processAzureDevOpsResponse("response"))
            .thenReturn(List.of(targetWorkItem));

        double expectedCompleted = 2.0 + 1.0;
        double expectedRemaining = 5.0 - 1.0;
        String expectedQuery = AzureDevOpsClient.UpdateWorkItemQueryCompletedAndRemaining(expectedRemaining, expectedCompleted);

        doNothing().when(azureDevOpsClient)
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));

        when(activityRecordRepository.save(any(ActivityRecord.class)))
            .thenReturn(record);

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1))
            .updateWorkItem(eq(1), eq(expectedQuery), eq(1L), eq("Board1"));
        verify(activityRecordRepository, times(1))
            .save(record);
        assertEquals(0, record.getStatus(), "O status do record deveria ser 0 após a atualização bem-sucedida.");
    }

   @Test
   void parseCompletedWork_parsesCorrectly() {
       double result = scheduledUpdateService.parseCompletedWork("1:30:00");
       assertEquals(1.5, result);
   }

   @Test
   void parseCompletedWork_handlesInvalidFormat() {
       assertThrows(NumberFormatException.class, () -> scheduledUpdateService.parseCompletedWork("invalid"));
   }
}