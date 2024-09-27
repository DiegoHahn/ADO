package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.ActivityRecordDTO;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final AzureDevOpsClient azureDevOpsClient;

    @Autowired
    public ActivityRecordService(ActivityRecordRepository activityRecordRepository, AzureDevOpsClient azureDevOpsClient) {
        this.activityRecordRepository = activityRecordRepository;
        this.azureDevOpsClient = azureDevOpsClient;
    }

    public ActivityRecord saveActivityRecord(ActivityRecordDTO dto) throws Exception {
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

        ActivityRecord savedRecord = activityRecordRepository.save(record);

        String updateQuery = AzureDevOpsClient.UpdateWorkItemQuery(
                record.getRemainingWork(),
                parseCompletedWork(record.getCompletedWork())
        );

        try {
            azureDevOpsClient.updateWorkItem(dto.getTask().getWorkItemId(), updateQuery);
        } catch (Exception e) {
            throw new Exception("Falha ao atualizar o WorkItem na Azure DevOps", e);
        }

        return savedRecord;
    }

    private double parseCompletedWork(String completedWork) {
        String[] timeParts = completedWork.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        return hours + (minutes / 60.0) + (seconds / 3600.0);
    }
}
