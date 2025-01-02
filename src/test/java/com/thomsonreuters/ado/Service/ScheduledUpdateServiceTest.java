// package com.thomsonreuters.ado.Service;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.Arrays;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import com.thomsonreuters.ado.Client.AzureDevOpsClient;
// import com.thomsonreuters.ado.Model.ActivityRecord;
// import com.thomsonreuters.ado.Model.TargetWorkItem;
// import com.thomsonreuters.ado.Model.UserInformation;
// import com.thomsonreuters.ado.Repository.ActivityRecordRepository;

// class ScheduledUpdateServiceTest {

//     @Mock
//     private ActivityRecordRepository activityRecordRepository;

//     @Mock
//     private AzureDevOpsClient azureDevOpsClient;

//     @Mock
//     private WorkItemService workItemService;

//     @InjectMocks
//     private ScheduledUpdateService scheduledUpdateService;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
// void updatePendingWorkItems_ShouldProcessPendingRecords() throws Exception {
//     // Arrange
//     ActivityRecord record1 = createActivityRecord(1001, "US123", "Active", "02:30:00", "Board1");
//     ActivityRecord record2 = createActivityRecord(1002, "US124", "Closed", "01:45:00", "Board2");
//     List<ActivityRecord> pendingRecords = Arrays.asList(record1, record2);

//     when(activityRecordRepository.findTop20ByStatus(1)).thenReturn(pendingRecords);

//     TargetWorkItem workItem1 = createTargetWorkItem(1001, 5.0, 10.0, 15.0);
//     TargetWorkItem workItem2 = createTargetWorkItem(1002, 3.0, 7.0, 10.0);

//     when(azureDevOpsClient.getWorItems(anyString(), anyLong(), anyString()))
//             .thenReturn("[{\"id\":1001},{\"id\":1002}]");

//     when(workItemService.processAzureDevOpsResponse(anyString()))
//             .thenReturn(Arrays.asList(workItem1, workItem2));

//     doNothing().when(azureDevOpsClient).updateWorkItem(anyInt(), anyString(), anyLong(), anyString());

//     // Act
//     scheduledUpdateService.updatePendingWorkItems();

//     // Assert
//     verify(activityRecordRepository, times(1)).findTop20ByStatus(1);
//     verify(azureDevOpsClient, times(2)).updateWorkItem(anyInt(), anyString(), anyLong(), anyString());
//     verify(activityRecordRepository, times(2)).save(any(ActivityRecord.class));
// }


//     @Test
//     void updatePendingWorkItems_ShouldProcessPendingRecords() throws Exception {
//         ActivityRecord record1 = new ActivityRecord();
//         record1.setStatus(1);
//         record1.setWorkItemId(1001);
//         record1.setUserStoryId("US123");
//         record1.setCurrentTrackedTime("02:30:00");
//         record1.setState("Active");
//         record1.setUserId(new UserInformation());
//         record1.setBoard("Board1");

//         ActivityRecord record2 = new ActivityRecord();
//         record2.setStatus(1);
//         record2.setWorkItemId(1002);
//         record2.setUserStoryId("US124");
//         record2.setCurrentTrackedTime("01:45:00");
//         record2.setState("Closed");
//         record2.setUserId(new UserInformation());
//         record2.setBoard("Board2");

//         List<ActivityRecord> pendingRecords = Arrays.asList(record1, record2);

//         when(activityRecordRepository.findTop20ByStatus(1)).thenReturn(pendingRecords);

//         TargetWorkItem workItem1 = new TargetWorkItem(0, null, null, null, null, null);
//         workItem1.setWorkItemId(1001);
//         workItem1.setCompletedWork(5.0);
//         workItem1.setRemainingWork(10.0);
//         workItem1.setOriginalEstimate(15.0);

//         TargetWorkItem workItem2 = new TargetWorkItem(0, null, null, null, null, null);
//         workItem2.setWorkItemId(1002);
//         workItem2.setCompletedWork(3.0);
//         workItem2.setRemainingWork(7.0);
//         workItem2.setOriginalEstimate(10.0);

//         when(azureDevOpsClient.getWorItems(anyString(), 1L, anyString()))
//                 .thenReturn("[{\"id\":1001},{\"id\":1002}]");

//         when(workItemService.processAzureDevOpsResponse(anyString()))
//                 .thenReturn(Arrays.asList(workItem1, workItem2));

//         doNothing().when(azureDevOpsClient).updateWorkItem(anyInt(), anyString(), 1L, anyString());

//         scheduledUpdateService.updatePendingWorkItems();

//         verify(activityRecordRepository, times(1)).findTop20ByStatus(1);
//         verify(azureDevOpsClient, times(2)).updateWorkItem(anyInt(), anyString(), 1L, anyString());
//         verify(activityRecordRepository, times(2)).save(any(ActivityRecord.class));
//     }

    // @Test
    // void updateWorkItem_ShouldUpdateRecordStatusTo0_OnSuccess() throws Exception {
    //     ActivityRecord record = new ActivityRecord();
    //     record.setStatus(1);
    //     record.setWorkItemId(1001);
    //     record.setUserStoryId("US123");
    //     record.setCurrentTrackedTime("02:30:00");
    //     record.setState("Active");
    //     record.setUserId(new User("user1"));
    //     record.setBoard("Board1");

    //     TargetWorkItem workItem = new TargetWorkItem();
    //     workItem.setWorkItemId(1001);
    //     workItem.setCompletedWork(5.0);
    //     workItem.setRemainingWork(10.0);
    //     workItem.setOriginalEstimate(15.0);

    //     when(azureDevOpsClient.getWorItems(anyString(), anyString(), anyString()))
    //             .thenReturn("[{\"id\":1001}]");

    //     when(workItemService.processAzureDevOpsResponse(anyString()))
    //             .thenReturn(Arrays.asList(workItem));

    //     doNothing().when(azureDevOpsClient).updateWorkItem(anyInt(), anyString(), anyString(), anyString());

    //     scheduledUpdateService.updatePendingWorkItems();

    //     ArgumentCaptor<ActivityRecord> captor = ArgumentCaptor.forClass(ActivityRecord.class);
    //     verify(activityRecordRepository).save(captor.capture());

    //     ActivityRecord savedRecord = captor.getValue();
    //     assertEquals(0, savedRecord.getStatus());
    // }

    // @Test
    // void updateWorkItem_ShouldUpdateRecordStatusTo2_OnException() throws Exception {
    //     ActivityRecord record = new ActivityRecord();
    //     record.setStatus(1);
    //     record.setWorkItemId(1001);
    //     record.setUserStoryId("US123");
    //     record.setCurrentTrackedTime("02:30:00");
    //     record.setState("Active");
    //     record.setUserId(new User("user1"));
    //     record.setBoard("Board1");

    //     when(activityRecordRepository.findTop20ByStatus(1)).thenReturn(Arrays.asList(record));

    //     when(azureDevOpsClient.getWorItems(anyString(), anyString(), anyString()))
    //             .thenThrow(new RuntimeException("API Error"));

    //     scheduledUpdateService.updatePendingWorkItems();

    //     ArgumentCaptor<ActivityRecord> captor = ArgumentCaptor.forClass(ActivityRecord.class);
    //     verify(activityRecordRepository).save(captor.capture());

    //     ActivityRecord savedRecord = captor.getValue();
    //     assertEquals(2, savedRecord.getStatus());
    // }
// }