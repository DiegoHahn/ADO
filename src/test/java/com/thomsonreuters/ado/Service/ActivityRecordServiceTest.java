package com.thomsonreuters.ado.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Model.ActivityRecordResponseDTO;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import com.thomsonreuters.ado.Repository.UserInformationRepository;

class ActivityRecordServiceTest {

    private ActivityRecordRepository activityRecordRepository;
    private UserInformationRepository userInformationRepository;
    private ActivityRecordService activityRecordService;

    @BeforeEach
    void setUp() {
        activityRecordRepository = Mockito.mock(ActivityRecordRepository.class);
        userInformationRepository = Mockito.mock(UserInformationRepository.class);
        activityRecordService = new ActivityRecordService(activityRecordRepository, userInformationRepository);
    }

    @Test
    void saveActivityRecord_SetsFields_WithNoTask() throws UserNotFoundException {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setBoard("testBoard");
        dto.setUserStoryId("123");
        dto.setConcluded(true);
        dto.setOriginalEstimate(5D);
        dto.setRemainingWork(3D);
        dto.setStartTime("2023-06-01T10:00:00Z");
        dto.setUserId(1L);

        UserInformation user = new UserInformation();
        when(userInformationRepository.findById(1L)).thenReturn(Optional.of(user));

        activityRecordService.saveActivityRecord(dto);

        ArgumentCaptor<ActivityRecord> captor = ArgumentCaptor.forClass(ActivityRecord.class);
        verify(activityRecordRepository).save(captor.capture());
        ActivityRecord savedRecord = captor.getValue();

        assertEquals("testBoard", savedRecord.getBoard());
        assertEquals("123", savedRecord.getUserStoryId());
        assertEquals(true, savedRecord.isConcluded());
        assertEquals(OffsetDateTime.parse("2023-06-01T10:00:00Z"), savedRecord.getStartTime());
        assertEquals(5, savedRecord.getOriginalEstimate());
        assertEquals(3, savedRecord.getRemainingWork());
        assertEquals(1, savedRecord.getStatus());
    }

    @Test
    void saveActivityRecord_SetsFields_WithTask() throws UserNotFoundException {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(2L);
        TargetWorkItem task = new TargetWorkItem(0, null, null, null, null, null);
        task.setWorkItemId(1001);
        task.setTitle("TestTask");
        task.setState("Active");
        task.setCompletedWork(2.5);
        dto.setTask(task);

        UserInformation user = new UserInformation();
        when(userInformationRepository.findById(2L)).thenReturn(Optional.of(user));

        activityRecordService.saveActivityRecord(dto);

        ArgumentCaptor<ActivityRecord> captor = ArgumentCaptor.forClass(ActivityRecord.class);
        verify(activityRecordRepository).save(captor.capture());
        ActivityRecord savedRecord = captor.getValue();

        assertEquals(1001, savedRecord.getWorkItemId());
        assertEquals("TestTask", savedRecord.getTitle());
        assertEquals("Active", savedRecord.getState());
        assertEquals("2.5", savedRecord.getCompletedWork());
    }

    @Test
    void testConvertToResponseDTO_ReflectionForCoverage() throws Exception {
        Method method = ActivityRecordService.class
            .getDeclaredMethod("convertToResponseDTO", ActivityRecord.class);
        method.setAccessible(true);

        ActivityRecord mockRecord = new ActivityRecord();
        mockRecord.setId(42L);
        mockRecord.setBoard("BoardTest");
        mockRecord.setUserStoryId("US123");
        mockRecord.setConcluded(true);
        mockRecord.setWorkItemId(999);
        mockRecord.setTitle("TestTitle");
        mockRecord.setState("TestState");
        mockRecord.setOriginalEstimate(4.0);
        mockRecord.setRemainingWork(3.0);
        mockRecord.setStartTime(OffsetDateTime.parse("2023-06-01T10:00:00Z"));
        mockRecord.setCompletedWork("2.5");
        mockRecord.setCurrentTrackedTime("01:00:00");
        mockRecord.setStatus(1);

        UserInformation user = new UserInformation();
        user.setUserId(7L);
        mockRecord.setUserId(user);

        ActivityRecordResponseDTO response =
            (ActivityRecordResponseDTO) method.invoke(activityRecordService, mockRecord);

        assertNotNull(response);
        assertEquals(42L, response.getId());
        assertEquals("BoardTest", response.getBoard());
        assertEquals("US123", response.getUserStoryId());
        assertEquals(true, response.isConcluded());
        assertEquals(999, response.getWorkItemId());
        assertEquals("TestTitle", response.getTitle());
        assertEquals("TestState", response.getState());
        assertEquals(4.0, response.getOriginalEstimate());
        assertEquals(3.0, response.getRemainingWork());
        assertEquals(OffsetDateTime.parse("2023-06-01T10:00:00Z"), response.getStartTime());
        assertEquals("2.5", response.getCompletedWork());
        assertEquals("01:00:00", response.getCurrentTrackedTime());
        assertEquals(1, response.getStatus());
        assertEquals(7L, response.getUserId());
    }

    @Test
    void saveActivityRecord_ThrowsUserNotFoundException() {
        ActivityRecordDTO dto = new ActivityRecordDTO();
        dto.setUserId(99L);
        when(userInformationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> activityRecordService.saveActivityRecord(dto));
    }

    @Test
    void updateActivityRecordsStatus_UpdatesStatusesForMatchingRecords() {
        Long userId = 1L;
        int oldStatus = 0;
        int newStatus = 1;

        ActivityRecord record1 = new ActivityRecord();
        record1.setStatus(oldStatus);
        ActivityRecord record2 = new ActivityRecord();
        record2.setStatus(oldStatus);

        List<ActivityRecord> records = Arrays.asList(record1, record2);
        when(activityRecordRepository.findByStatusAndUserId(oldStatus, userId)).thenReturn(records);

        activityRecordService.updateActivityRecordsStatus(userId, oldStatus, newStatus);

        ArgumentCaptor<ActivityRecord> captor = ArgumentCaptor.forClass(ActivityRecord.class);
        verify(activityRecordRepository, times(2)).save(captor.capture());
        List<ActivityRecord> savedRecords = captor.getAllValues();

        assertEquals(newStatus, savedRecords.get(0).getStatus());
        assertEquals(newStatus, savedRecords.get(1).getStatus());
        verify(activityRecordRepository).findByStatusAndUserId(oldStatus, userId);
    }

    @Test
    void testGetActivityRecordsByDate_ReturnsExpectedList() {
        Long userId = 10L;
        String date = "2023-06-10";
        ActivityRecord record1 = new ActivityRecord();
        record1.setId(1L);
        record1.setBoard("BoardA");
        ActivityRecord record2 = new ActivityRecord();
        record2.setId(2L);
        record2.setBoard("BoardB");

        when(activityRecordRepository.findByDate(userId, date))
            .thenReturn(Arrays.asList(record1, record2));

        List<ActivityRecordResponseDTO> responseList = activityRecordService.getActivityRecordsByDate(userId, date);

        assertEquals(2, responseList.size());
        assertEquals(1L, responseList.get(0).getId());
        assertEquals("BoardA", responseList.get(0).getBoard());
        assertEquals(2L, responseList.get(1).getId());
        assertEquals("BoardB", responseList.get(1).getBoard());
        verify(activityRecordRepository, times(1)).findByDate(userId, date);
    }

    @Test
    void testGetActivityRecordsByWorkItemID_ReturnsExpectedList() {
        Long userId = 20L;
        int workItemId = 200;
        ActivityRecord record1 = new ActivityRecord();
        record1.setId(3L);
        record1.setWorkItemId(workItemId);
        ActivityRecord record2 = new ActivityRecord();
        record2.setId(4L);
        record2.setWorkItemId(workItemId);

        when(activityRecordRepository.findByWorkItemId(userId, workItemId))
            .thenReturn(Arrays.asList(record1, record2));

        List<ActivityRecordResponseDTO> responseList =
            activityRecordService.getActivityRecordsByWorkItemID(userId, workItemId);

        assertEquals(2, responseList.size());
        assertEquals(3L, responseList.get(0).getId());
        assertEquals(200, responseList.get(0).getWorkItemId());
        assertEquals(4L, responseList.get(1).getId());
        assertEquals(200, responseList.get(1).getWorkItemId());
        verify(activityRecordRepository, times(1)).findByWorkItemId(userId, workItemId);
    }
}
