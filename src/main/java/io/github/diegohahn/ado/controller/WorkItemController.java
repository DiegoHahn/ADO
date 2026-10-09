package io.github.diegohahn.ado.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.diegohahn.ado.client.AzureDevOpsClient;
import io.github.diegohahn.ado.exceptions.AzureDevOpsApiException;
import io.github.diegohahn.ado.exceptions.InvalidTokenException;
import io.github.diegohahn.ado.exceptions.UserNotFoundException;
import io.github.diegohahn.ado.model.TargetWorkItem;
import io.github.diegohahn.ado.model.UserStoryRequest;
import io.github.diegohahn.ado.service.WorkItemService;

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
    public ResponseEntity<List<TargetWorkItem>> getTargetWorkItemsForUserStory(@RequestBody UserStoryRequest request)
            throws UserNotFoundException, InvalidTokenException, AzureDevOpsApiException, IOException, InterruptedException, URISyntaxException {
        String azureDevOpsResponse = azureDevOpsClient.getWorItems(request.getUserStoryId(), request.getUserId(), request.getBoard());
        try {
            List<TargetWorkItem> targetWorkItems = workItemService.processAzureDevOpsResponse(azureDevOpsResponse, request);
            return ResponseEntity.ok(targetWorkItems);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}