package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScheduledUpdateService {

    private final ActivityRecordRepository activityRecordRepository;
    private final AzureDevOpsClient azureDevOpsClient;

    @Autowired
    public ScheduledUpdateService(ActivityRecordRepository activityRecordRepository, AzureDevOpsClient azureDevOpsClient) {
        this.activityRecordRepository = activityRecordRepository;
        this.azureDevOpsClient = azureDevOpsClient;
    }

    @Scheduled(fixedRate = 50000)
    public void updatePendingWorkItems() {

        List<ActivityRecord> pendingRecords = activityRecordRepository.findTop20ByStatus(1);

        for (ActivityRecord record : pendingRecords) {
            try {
                Double updatedRemaingWork = record.getRemainingWork() - parseCompletedWork(record.getCompletedWork());
                String updateQuery = AzureDevOpsClient.UpdateWorkItemQuery(
                        updatedRemaingWork,
                        parseCompletedWork(record.getCompletedWork())

                );
                System.out.println(record.getRemainingWork() + " - " + parseCompletedWork(record.getCompletedWork()) + " = " + updatedRemaingWork);

                azureDevOpsClient.updateWorkItem(record.getWorkItemId(), updateQuery, record.getUserId().getUserId());

                // Se não ocorreu erro atualiza o status para 0
                record.setStatus(0);

            } catch (Exception e) {
                record.setStatus(2); // status de erro
                System.err.println("Falha ao atualizar o WorkItem no ADO: " + e.getMessage());
            } finally {
                // Salva no banco independentemente do resultado
                activityRecordRepository.save(record);
            }
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
