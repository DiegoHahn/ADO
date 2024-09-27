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
        ActivityRecord record = new ActivityRecord(
                dto.getId(),
                dto.getBoard(),
                dto.getUserStoryId(),
                dto.isConcluded(),
                dto.getTask().getWorkItemId(),
                dto.getTask().getAssignedToUserSK(),
                dto.getTask().getTitle(),
                dto.getTask().getState(),
                dto.getOriginalEstimate(),
                dto.getRemainingWork(),
                LocalTime.parse(dto.getStartTime()),
                dto.getCompletedWork()
        );
        return activityRecordRepository.save(record);
    }
}