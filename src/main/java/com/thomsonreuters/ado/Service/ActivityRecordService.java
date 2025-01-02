package com.thomsonreuters.ado.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Model.ActivityRecordResponseDTO;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import com.thomsonreuters.ado.Repository.UserInformationRepository;

@Service
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final UserInformationRepository userInformationRepository;

    @Autowired
    public ActivityRecordService(ActivityRecordRepository activityRecordRepository, UserInformationRepository userInformationRepository) {
        this.activityRecordRepository = activityRecordRepository;
        this.userInformationRepository = userInformationRepository;
    }

    public ActivityRecord saveActivityRecord(ActivityRecordDTO dto) throws UserNotFoundException {
        ActivityRecord record = new ActivityRecord();

        record.setBoard(dto.getBoard());
        record.setUserStoryId(dto.getUserStoryId());
        record.setConcluded(dto.isConcluded());

        if (dto.getTask() != null) {
            record.setWorkItemId(dto.getTask().getWorkItemId());
            record.setTitle(dto.getTask().getTitle());
            record.setState(dto.getTask().getState());
            record.setCompletedWork(String.valueOf(dto.getTask().getCompletedWork()));
        }

        record.setOriginalEstimate(dto.getOriginalEstimate());
        record.setRemainingWork(dto.getRemainingWork());

        if (dto.getStartTime() != null) {
            record.setStartTime(OffsetDateTime.parse(dto.getStartTime()));
        }

        record.setCurrentTrackedTime(dto.getcurrentTrackedTime());
        record.setStatus(1);

        UserInformation user = userInformationRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + dto.getUserId()));

        record.setUserId(user);
        return activityRecordRepository.save(record);
    }

    private ActivityRecordResponseDTO convertToResponseDTO(ActivityRecord record) {
        ActivityRecordResponseDTO dto = new ActivityRecordResponseDTO();
        dto.setId(record.getId());
        dto.setBoard(record.getBoard());
        dto.setUserStoryId(record.getUserStoryId());
        dto.setConcluded(record.isConcluded());
        dto.setWorkItemId(record.getWorkItemId());
        dto.setTitle(record.getTitle());
        dto.setState(record.getState());
        dto.setOriginalEstimate(record.getOriginalEstimate());
        dto.setRemainingWork(record.getRemainingWork());
        dto.setStartTime(record.getStartTime());
        dto.setCompletedWork(record.getCompletedWork());
        dto.setCurrentTrackedTime(record.getCurrentTrackedTime());
        dto.setStatus(record.getStatus());
        
        if (record.getUserId() != null) {
            dto.setUserId(record.getUserId().getUserId());
        } else {
            dto.setUserId(null);
        }

        return dto;
    }

    public void updateActivityRecordsStatus(Long userId, int oldStatus, int newStatus) {
        List<ActivityRecord> records = activityRecordRepository.findByStatusAndUserId(oldStatus, userId);
        for (ActivityRecord record : records) {
            record.setStatus(newStatus);
            activityRecordRepository.save(record);
        }
    }

public List<ActivityRecordResponseDTO> getActivityRecordsByDate(Long userId, String date) {
    List<ActivityRecord> activityRecords = activityRecordRepository.findByDate(userId, date);
    return activityRecords.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
}

    public List<ActivityRecordResponseDTO> getActivityRecordsByWorkItemID(Long userId, int workItemId) {
        List<ActivityRecord> activityRecords = activityRecordRepository.findByWorkItemId(userId, workItemId);
        return activityRecords.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}
