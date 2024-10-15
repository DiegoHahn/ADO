package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Model.UserInformation;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import com.thomsonreuters.ado.Repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final UserInformationRepository userInformationRepository;

    @Autowired
    public ActivityRecordService(ActivityRecordRepository activityRecordRepository, UserInformationRepository userInformationRepository) {
        this.activityRecordRepository = activityRecordRepository;
        this.userInformationRepository = userInformationRepository;
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

        UserInformation user = userInformationRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + dto.getUserId())); //todo criar uma exceção personalizada para usuario nao encontrado

        record.setUserId(user);
        return activityRecordRepository.save(record);
    }
}
