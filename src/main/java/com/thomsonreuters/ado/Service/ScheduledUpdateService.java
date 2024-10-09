package com.thomsonreuters.ado.Service;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.ActivityRecord;
import com.thomsonreuters.ado.Repository.ActivityRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

//@Service
//public class ScheduledUpdateService {
//
//    private final ActivityRecordRepository activityRecordRepository;
//    private final AzureDevOpsClient azureDevOpsClient;
//
//    @Autowired
//    public ScheduledUpdateService(ActivityRecordRepository activityRecordRepository, AzureDevOpsClient azureDevOpsClient) {
//        this.activityRecordRepository = activityRecordRepository;
//        this.azureDevOpsClient = azureDevOpsClient;
//    }
//
//    @Scheduled(fixedRate = 6000) // Executa a cada 10 minutos (600000 ms)
//    public void updatePendingWorkItems() {
//        List<ActivityRecord> pendingRecords = activityRecordRepository.findByStatus(1);
//
//        for (ActivityRecord record : pendingRecords) {
//            try {
//                String updateQuery = AzureDevOpsClient.UpdateWorkItemQuery(
//                        record.getRemainingWork(),
//                        parseCompletedWork(record.getCompletedWork())
//                );
//                azureDevOpsClient.updateWorkItem(record.getWorkItemId(), updateQuery);
//                if (record.getCompletedWork() != null) {
//                break;
//                } else {
//                    record.setStatus(0);
//                }
//                record.setStatus(0); // Exemplo de status atualizado para 0 (sucesso)
//                activityRecordRepository.save(record);
//
//            } catch (Exception e) {
//                System.err.println("Falha ao atualizar o WorkItem no ADO: " + e.getMessage());
//            }
//        }
//    }
//
//    private Double parseCompletedWork(String completedWork) {
//        return completedWork != null ? Double.parseDouble(completedWork) : 0.0;
//    }
//}
