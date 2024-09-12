package com.thomsonreuters.ado.Controller;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.service.WorkItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workitems")
public class WorkItemController {
    private final WorkItemService workItemService;
    private final AzureDevOpsClient azureDevOpsClient;

    public WorkItemController(WorkItemService workItemService, AzureDevOpsClient azureDevOpsClient) {
        this.workItemService = workItemService;
        this.azureDevOpsClient = azureDevOpsClient;
    }

    @GetMapping("/userstory/{id}")
    public ResponseEntity<List<TargetWorkItem>> getTargetWorkItemsForUserStory(@PathVariable String id) throws Exception {
        String azureDevOpsResponse = azureDevOpsClient.getWorItems(id, "5cfc3fc9-fb2c-4809-9586-44ffce7c24ca");

        try {
            List<TargetWorkItem> targetWorkItems = workItemService.processAzureDevOpsResponse(azureDevOpsResponse);
            return ResponseEntity.ok(targetWorkItems);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}