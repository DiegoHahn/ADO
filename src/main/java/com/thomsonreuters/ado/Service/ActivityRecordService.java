package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;

    @Autowired
    public ActivityRecordService(ActivityRecordRepository activityRecordRepository) {
        this.activityRecordRepository = activityRecordRepository;
    }

    public ActivityRecord saveActivityRecord(ActivityRecordDTO dto) {
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
            record.setStartTime(LocalTime.parse(dto.getStartTime()));
        }

        record.setCompletedWork(dto.getCompletedWork());
        record.setStatus(1);

        return activityRecordRepository.save(record);
    }
}
