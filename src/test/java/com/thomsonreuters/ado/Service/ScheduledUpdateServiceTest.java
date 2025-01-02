package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduledUpdateServiceTest {

    @Mock
    private ActivityRecordRepository activityRecordRepository;

    @Mock
    private AzureDevOpsClient azureDevOpsClient;

    @Mock
    private WorkItemService workItemService;

    @InjectMocks
    private ScheduledUpdateService scheduledUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

        TargetWorkItem targetWorkItem = new TargetWorkItem();
        targetWorkItem.setWorkItemId(1);
        targetWorkItem.setCompletedWork(2.0);
        targetWorkItem.setRemainingWork(3.0);

        when(azureDevOpsClient.getWorItems(anyString(), 1L, anyString())).thenReturn("response");
        when(workItemService.processAzureDevOpsResponse(anyString())).thenReturn(List.of(targetWorkItem));
        doNothing().when(azureDevOpsClient).updateWorkItem(anyInt(), anyString(), 1L, anyString());
        doNothing().when(activityRecordRepository).save(any(ActivityRecord.class));

        scheduledUpdateService.updateWorkItem(record);

        verify(azureDevOpsClient, times(1)).updateWorkItem(anyInt(), anyString(), 1L, anyString());
        verify(activityRecordRepository, times(1)).save(record);
        assertEquals(0, record.getStatus());
    }
//
//    @Test
//    void updateWorkItem_handlesWorkItemNotFound() throws Exception {
//        ActivityRecord record = new ActivityRecord();
//        record.setWorkItemId(1);
//        record.setCurrentTrackedTime("1:00:00");
//
//        when(azureDevOpsClient.getWorItems(anyInt(), anyInt(), anyString())).thenReturn("response");
//        when(workItemService.processAzureDevOpsResponse(anyString())).thenReturn(List.of());
//
//        scheduledUpdateService.updateWorkItem(record);
//
//        verify(activityRecordRepository, times(1)).save(record);
//        assertEquals(2, record.getStatus());
//    }
//
//    @Test
//    void parseCompletedWork_parsesCorrectly() {
//        double result = scheduledUpdateService.parseCompletedWork("1:30:00");
//        assertEquals(1.5, result);
//    }
//
//    @Test
//    void parseCompletedWork_handlesInvalidFormat() {
//        assertThrows(NumberFormatException.class, () -> scheduledUpdateService.parseCompletedWork("invalid"));
//    }
}