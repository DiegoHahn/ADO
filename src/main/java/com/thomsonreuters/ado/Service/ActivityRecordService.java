package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Exceptions.UserNotFoundException;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import com.thomsonreuters.ado.Repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

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
            record.setAssignedToUserSK(dto.getTask().getAssignedToUserSK());
            record.setTitle(dto.getTask().getTitle());
            record.setState(dto.getTask().getState());
        }

        record.setOriginalEstimate(dto.getOriginalEstimate());
        record.setRemainingWork(dto.getRemainingWork());

        if (dto.getStartTime() != null) {
            record.setStartTime(OffsetDateTime.parse(dto.getStartTime()));
        }

        record.setCompletedWork(dto.getCompletedWork());
        record.setStatus(1);

        UserInformation user = userInformationRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + dto.getUserId()));

        record.setUserId(user);
        return activityRecordRepository.save(record);
    }

    //todo:filtrar por data na query?
    public List<ActivityRecord> getActivityRecordsByUserId(Long userId) {
        return activityRecordRepository.findByStatusAndUserId(0, userId);
    }

    public void updateActivityRecordsStatus(Long userId, int oldStatus, int newStatus) {
        List<ActivityRecord> records = activityRecordRepository.findByStatusAndUserId(oldStatus, userId);
        for (ActivityRecord record : records) {
            record.setStatus(newStatus);
            activityRecordRepository.save(record);
        }
    }
}
