package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduledUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledUpdateService.class);

    private final ActivityRecordRepository activityRecordRepository;
    private final AzureDevOpsClient azureDevOpsClient;
    private final WorkItemService workItemService;

    @Autowired
    public ScheduledUpdateService(
            ActivityRecordRepository activityRecordRepository,
            AzureDevOpsClient azureDevOpsClient,
            WorkItemService workItemService
    ) {
        this.activityRecordRepository = activityRecordRepository;
        this.azureDevOpsClient = azureDevOpsClient;
        this.workItemService = workItemService;
    }

    @Scheduled(fixedRate = 5000)
    public void updatePendingWorkItems() {
        List<ActivityRecord> pendingRecords = activityRecordRepository.findTop20ByStatus(1);

        for (ActivityRecord record : pendingRecords) {
            updateWorkItem(record);
        }
    }

    private void updateWorkItem(ActivityRecord record) {
        try {
            Optional<TargetWorkItem> targetWorkItemOpt = findTargetWorkItem(record);

            if (targetWorkItemOpt.isEmpty()) {
                throw new Exception("WorkItem não encontrada com o ID: " + record.getWorkItemId());
            }

            TargetWorkItem targetWorkItem = targetWorkItemOpt.get();
            double parsedTrackedTime = parseCompletedWork(record.getCurrentTrackedTime());

            double updatedCompletedWork = calculateUpdatedCompletedWork(targetWorkItem, record.getCurrentTrackedTime());
            Double updatedRemainingWork = calculateUpdatedRemainingWork(targetWorkItem, parsedTrackedTime);

            String updateQuery = (updatedRemainingWork == null)
                    ? AzureDevOpsClient.UpdateWorkItemQueryCompleted(updatedCompletedWork)
                    : AzureDevOpsClient.UpdateWorkItemQueryCompletedAndRemaining(updatedRemainingWork, updatedCompletedWork);

            azureDevOpsClient.updateWorkItem(
                    record.getWorkItemId(),
                    updateQuery,
                    record.getUserId().getUserId(),
                    record.getBoard()
            );

            record.setStatus(0);
        } catch (Exception e) {
            record.setStatus(2);
            logger.error("Falha ao atualizar WorkItem no ADO: {}", e.getMessage());
        } finally {
            activityRecordRepository.save(record);
        }
    }

    private Optional<TargetWorkItem> findTargetWorkItem(ActivityRecord record) throws Exception {
        String azureDevOpsResponse = azureDevOpsClient.getWorItems(
                record.getUserStoryId(),
                record.getUserId().getUserId(),
                record.getBoard()
        );
        List<TargetWorkItem> targetWorkItems = workItemService.processAzureDevOpsResponse(azureDevOpsResponse);

        return targetWorkItems.stream()
                .filter(item -> item.getWorkItemId() == record.getWorkItemId())
                .findFirst();
    }

    private double calculateUpdatedCompletedWork(TargetWorkItem targetWorkItem, String currentTrackedTime) {
        return targetWorkItem.getCompletedWork() + parseCompletedWork(currentTrackedTime);
    }

    private Double calculateUpdatedRemainingWork(TargetWorkItem targetWorkItem, double parsedTrackedTime) {
        Double remainingWork = targetWorkItem.getRemainingWork();
        Double originalEstimate = targetWorkItem.getOriginalEstimate();

        if (remainingWork != null) {
            return remainingWork - parsedTrackedTime;
        } else if (originalEstimate != null) {
            return originalEstimate - parsedTrackedTime;
        } else {
            return null;
        }
    }

    private double parseCompletedWork(String completedWork) {
        String[] timeParts = completedWork.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        return hours + (minutes / 60.0) + (seconds / 3600.0);
    }
}