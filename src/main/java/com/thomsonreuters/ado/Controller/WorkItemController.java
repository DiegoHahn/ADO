package com.thomsonreuters.ado.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomsonreuters.ado.Client.AzureDevOpsClient;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Model.UserStoryRequest;
import com.thomsonreuters.ado.Service.WorkItemService;

@RestController
@RequestMapping("/workitems")
public class WorkItemController {
    private final WorkItemService workItemService;
    private final AzureDevOpsClient azureDevOpsClient;

    public WorkItemController(WorkItemService workItemService, AzureDevOpsClient azureDevOpsClient) {
        this.workItemService = workItemService;
        this.azureDevOpsClient = azureDevOpsClient;
    }

    @PostMapping("/userstory")
    public ResponseEntity<List<TargetWorkItem>> getTargetWorkItemsForUserStory(@RequestBody UserStoryRequest request) throws Exception {
        String azureDevOpsResponse = azureDevOpsClient.getWorItems(request.getUserStoryId(), request.getUserId(), request.getBoard());
        try {
            List<TargetWorkItem> targetWorkItems = workItemService.processAzureDevOpsResponse(azureDevOpsResponse, request);
            return ResponseEntity.ok(targetWorkItems);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}