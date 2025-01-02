package com.thomsonreuters.ado.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import com.thomsonreuters.ado.Model.UserStoryRequest;

class WorkItemServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WorkItemService workItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessAzureDevOpsResponse_WithConcluded() throws JsonProcessingException {
        String jsonResponse = "{\"value\":[]}";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(true);

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);

        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.<JsonNode>of().iterator());

        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithoutConcluded() throws JsonProcessingException {
        String jsonResponse = "{\"value\":[]}";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);

        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);

        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.<JsonNode>of().iterator());

        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessAzureDevOpsResponse_NoUserStoryRequest() throws JsonProcessingException {
        String jsonResponse = "{\"value\":[]}";
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.<JsonNode>of().iterator());
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testProcessAzureDevOpsResponse_WithData_ConcludedTrue_StateNotClosed() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ { \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 101, \"Title\": \"Task 101\", \"State\": \"Active\", \"OriginalEstimate\": 5.0, \"RemainingWork\": 3.0, \"CompletedWork\": 2.0 } } ] } ] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(true);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode = mock(JsonNode.class);
        JsonNode linksNode = mock(JsonNode.class);
        JsonNode linkNode = mock(JsonNode.class);
        JsonNode targetWorkItemNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode).iterator());
        when(itemNode.get("Links")).thenReturn(linksNode);
        when(linksNode.iterator()).thenReturn(List.of(linkNode).iterator());
        when(linkNode.get("TargetWorkItem")).thenReturn(targetWorkItemNode);
        when(targetWorkItemNode.get("WorkItemId")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("WorkItemId").asInt()).thenReturn(101);
        when(targetWorkItemNode.get("Title")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("Title").asText()).thenReturn("Task 101");
        when(targetWorkItemNode.get("State")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("State").asText()).thenReturn("Active");
        when(targetWorkItemNode.get("OriginalEstimate")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("OriginalEstimate").isNull()).thenReturn(false);
        when(targetWorkItemNode.get("OriginalEstimate").asDouble()).thenReturn(5.0);
        when(targetWorkItemNode.get("RemainingWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("RemainingWork").isNull()).thenReturn(false);
        when(targetWorkItemNode.get("RemainingWork").asDouble()).thenReturn(3.0);
        when(targetWorkItemNode.get("CompletedWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("CompletedWork").isNull()).thenReturn(false);
        when(targetWorkItemNode.get("CompletedWork").asDouble()).thenReturn(2.0);
        
        // Act
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(101, item.getWorkItemId());
        assertEquals("Task 101", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(5.0, item.getOriginalEstimate());
        assertEquals(3.0, item.getRemainingWork());
        assertEquals(2.0, item.getCompletedWork());
    }
    
    @Test
    void testProcessAzureDevOpsResponse_WithData_ConcludedTrue_StateClosed() throws JsonProcessingException {
        // Arrange
        String jsonResponse = "{ \"value\": [ { \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 102, \"Title\": \"Task 102\", \"State\": \"Closed\", \"OriginalEstimate\": 8.0, \"RemainingWork\": 0.0, \"CompletedWork\": 8.0 } } ] } ] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(true);
        
        // Mock JsonNode hierarchy
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode = mock(JsonNode.class);
        JsonNode linksNode = mock(JsonNode.class);
        JsonNode linkNode = mock(JsonNode.class);
        JsonNode targetWorkItemNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode).iterator());
        when(itemNode.get("Links")).thenReturn(linksNode);
        when(linksNode.iterator()).thenReturn(List.of(linkNode).iterator());
        when(linkNode.get("TargetWorkItem")).thenReturn(targetWorkItemNode);
        when(targetWorkItemNode.get("WorkItemId")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("WorkItemId").asInt()).thenReturn(102);
        when(targetWorkItemNode.get("Title")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("Title").asText()).thenReturn("Task 102");
        when(targetWorkItemNode.get("State")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("State").asText()).thenReturn("Closed");
        when(targetWorkItemNode.get("OriginalEstimate")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("OriginalEstimate").isNull()).thenReturn(false);
        when(targetWorkItemNode.get("OriginalEstimate").asDouble()).thenReturn(8.0);
        when(targetWorkItemNode.get("RemainingWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("RemainingWork").isNull()).thenReturn(false);
        when(targetWorkItemNode.get("RemainingWork").asDouble()).thenReturn(0.0);
        when(targetWorkItemNode.get("CompletedWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode.get("CompletedWork").isNull()).thenReturn(false);
        when(targetWorkItemNode.get("CompletedWork").asDouble()).thenReturn(8.0);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testProcessAzureDevOpsResponse_WithData_ConcludedFalse() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
                "{ \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 103, \"Title\": \"Task 103\", \"State\": \"Active\", \"OriginalEstimate\": 10.0, \"RemainingWork\": 5.0, \"CompletedWork\": 5.0 } } ] }," +
                "{ \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 104, \"Title\": \"Task 104\", \"State\": \"Closed\", \"OriginalEstimate\": 15.0, \"RemainingWork\": 0.0, \"CompletedWork\": 15.0 } } ] }" +
                "] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode1 = mock(JsonNode.class);
        JsonNode linksNode1 = mock(JsonNode.class);
        JsonNode linkNode1 = mock(JsonNode.class);
        JsonNode targetWorkItemNode1 = mock(JsonNode.class);
        
        JsonNode itemNode2 = mock(JsonNode.class);
        JsonNode linksNode2 = mock(JsonNode.class);
        JsonNode linkNode2 = mock(JsonNode.class);
        JsonNode targetWorkItemNode2 = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode1, itemNode2).iterator());
        
        when(itemNode1.get("Links")).thenReturn(linksNode1);
        when(linksNode1.iterator()).thenReturn(List.of(linkNode1).iterator());
        when(linkNode1.get("TargetWorkItem")).thenReturn(targetWorkItemNode1);
        when(targetWorkItemNode1.get("WorkItemId")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("WorkItemId").asInt()).thenReturn(103);
        when(targetWorkItemNode1.get("Title")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("Title").asText()).thenReturn("Task 103");
        when(targetWorkItemNode1.get("State")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("State").asText()).thenReturn("Active");
        when(targetWorkItemNode1.get("OriginalEstimate")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("OriginalEstimate").isNull()).thenReturn(false);
        when(targetWorkItemNode1.get("OriginalEstimate").asDouble()).thenReturn(10.0);
        when(targetWorkItemNode1.get("RemainingWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("RemainingWork").isNull()).thenReturn(false);
        when(targetWorkItemNode1.get("RemainingWork").asDouble()).thenReturn(5.0);
        when(targetWorkItemNode1.get("CompletedWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("CompletedWork").isNull()).thenReturn(false);
        when(targetWorkItemNode1.get("CompletedWork").asDouble()).thenReturn(5.0);
        
        when(itemNode2.get("Links")).thenReturn(linksNode2);
        when(linksNode2.iterator()).thenReturn(List.of(linkNode2).iterator());
        when(linkNode2.get("TargetWorkItem")).thenReturn(targetWorkItemNode2);
        when(targetWorkItemNode2.get("WorkItemId")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("WorkItemId").asInt()).thenReturn(104);
        when(targetWorkItemNode2.get("Title")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("Title").asText()).thenReturn("Task 104");
        when(targetWorkItemNode2.get("State")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("State").asText()).thenReturn("Closed");
        when(targetWorkItemNode2.get("OriginalEstimate")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("OriginalEstimate").isNull()).thenReturn(false);
        when(targetWorkItemNode2.get("OriginalEstimate").asDouble()).thenReturn(15.0);
        when(targetWorkItemNode2.get("RemainingWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("RemainingWork").isNull()).thenReturn(false);
        when(targetWorkItemNode2.get("RemainingWork").asDouble()).thenReturn(0.0);
        when(targetWorkItemNode2.get("CompletedWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("CompletedWork").isNull()).thenReturn(false);
        when(targetWorkItemNode2.get("CompletedWork").asDouble()).thenReturn(15.0);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        TargetWorkItem item1 = result.get(0);
        assertEquals(103, item1.getWorkItemId());
        assertEquals("Task 103", item1.getTitle());
        assertEquals("Active", item1.getState());
        
        TargetWorkItem item2 = result.get(1);
        assertEquals(104, item2.getWorkItemId());
        assertEquals("Task 104", item2.getTitle());
        assertEquals("Closed", item2.getState());
    }
    
    @Test
    void testProcessAzureDevOpsResponse_NoUserStoryRequest_WithData() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
                "{ \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 105, \"Title\": \"Task 105\", \"State\": \"Active\", \"OriginalEstimate\": 7.0, \"RemainingWork\": 2.0, \"CompletedWork\": 5.0 } } ] }," +
                "{ \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 106, \"Title\": \"Task 106\", \"State\": \"Closed\", \"OriginalEstimate\": 9.0, \"RemainingWork\": 0.0, \"CompletedWork\": 9.0 } } ] }" +
                "] }";
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode1 = mock(JsonNode.class);
        JsonNode linksNode1 = mock(JsonNode.class);
        JsonNode linkNode1 = mock(JsonNode.class);
        JsonNode targetWorkItemNode1 = mock(JsonNode.class);
        
        JsonNode itemNode2 = mock(JsonNode.class);
        JsonNode linksNode2 = mock(JsonNode.class);
        JsonNode linkNode2 = mock(JsonNode.class);
        JsonNode targetWorkItemNode2 = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode1, itemNode2).iterator());
        
        when(itemNode1.get("Links")).thenReturn(linksNode1);
        when(linksNode1.iterator()).thenReturn(List.of(linkNode1).iterator());
        when(linkNode1.get("TargetWorkItem")).thenReturn(targetWorkItemNode1);
        when(targetWorkItemNode1.get("WorkItemId")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("WorkItemId").asInt()).thenReturn(105);
        when(targetWorkItemNode1.get("Title")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("Title").asText()).thenReturn("Task 105");
        when(targetWorkItemNode1.get("State")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("State").asText()).thenReturn("Active");
        when(targetWorkItemNode1.get("OriginalEstimate")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("OriginalEstimate").isNull()).thenReturn(false);
        when(targetWorkItemNode1.get("OriginalEstimate").asDouble()).thenReturn(7.0);
        when(targetWorkItemNode1.get("RemainingWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("RemainingWork").isNull()).thenReturn(false);
        when(targetWorkItemNode1.get("RemainingWork").asDouble()).thenReturn(2.0);
        when(targetWorkItemNode1.get("CompletedWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode1.get("CompletedWork").isNull()).thenReturn(false);
        when(targetWorkItemNode1.get("CompletedWork").asDouble()).thenReturn(5.0);
        
        when(itemNode2.get("Links")).thenReturn(linksNode2);
        when(linksNode2.iterator()).thenReturn(List.of(linkNode2).iterator());
        when(linkNode2.get("TargetWorkItem")).thenReturn(targetWorkItemNode2);
        when(targetWorkItemNode2.get("WorkItemId")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("WorkItemId").asInt()).thenReturn(106);
        when(targetWorkItemNode2.get("Title")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("Title").asText()).thenReturn("Task 106");
        when(targetWorkItemNode2.get("State")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("State").asText()).thenReturn("Closed");
        when(targetWorkItemNode2.get("OriginalEstimate")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("OriginalEstimate").isNull()).thenReturn(false);
        when(targetWorkItemNode2.get("OriginalEstimate").asDouble()).thenReturn(9.0);
        when(targetWorkItemNode2.get("RemainingWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("RemainingWork").isNull()).thenReturn(false);
        when(targetWorkItemNode2.get("RemainingWork").asDouble()).thenReturn(0.0);
        when(targetWorkItemNode2.get("CompletedWork")).thenReturn(mock(JsonNode.class));
        when(targetWorkItemNode2.get("CompletedWork").isNull()).thenReturn(false);
        when(targetWorkItemNode2.get("CompletedWork").asDouble()).thenReturn(9.0);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse);
        
        assertNotNull(result);
        assertEquals(2, result.size());

        TargetWorkItem item1 = result.get(0);
        assertEquals(105, item1.getWorkItemId());
        assertEquals("Task 105", item1.getTitle());
        assertEquals("Active", item1.getState());

        TargetWorkItem item2 = result.get(1);
        assertEquals(106, item2.getWorkItemId());
        assertEquals("Task 106", item2.getTitle());
        assertEquals("Closed", item2.getState());
    }
    
    @Test
    void testProcessAzureDevOpsResponse_WithMissingLinks() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
                "{ \"SomeOtherField\": \"Value1\" }," +
                "{ \"Links\": null }" +
                "] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode1 = mock(JsonNode.class);
        JsonNode itemNode2 = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode1, itemNode2).iterator());
        
        when(itemNode1.get("Links")).thenReturn(mock(JsonNode.class));
        when(valueNode.iterator()).thenReturn(List.<JsonNode>of().iterator());
        
        when(itemNode2.get("Links")).thenReturn(null);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithNullFields() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ { \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 201, \"Title\": \"Task 201\", \"State\": \"Active\", \"OriginalEstimate\": null, \"RemainingWork\": null, \"CompletedWork\": null } } ] } ] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode = mock(JsonNode.class);
        JsonNode linksNode = mock(JsonNode.class);
        JsonNode linkNode = mock(JsonNode.class);
        JsonNode targetWorkItemNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode).iterator());
        when(itemNode.get("Links")).thenReturn(linksNode);
        when(linksNode.iterator()).thenReturn(List.of(linkNode).iterator());
        when(linkNode.get("TargetWorkItem")).thenReturn(targetWorkItemNode);
        
        JsonNode workItemIdNode = mock(JsonNode.class);
        JsonNode titleNode = mock(JsonNode.class);
        JsonNode stateNode = mock(JsonNode.class);
        JsonNode originalEstimateNode = mock(JsonNode.class);
        JsonNode remainingWorkNode = mock(JsonNode.class);
        JsonNode completedWorkNode = mock(JsonNode.class);
        
        when(targetWorkItemNode.get("WorkItemId")).thenReturn(workItemIdNode);
        when(workItemIdNode.asInt()).thenReturn(201);
        
        when(targetWorkItemNode.get("Title")).thenReturn(titleNode);
        when(titleNode.asText()).thenReturn("Task 201");
        
        when(targetWorkItemNode.get("State")).thenReturn(stateNode);
        when(stateNode.asText()).thenReturn("Active");
        
        when(targetWorkItemNode.get("OriginalEstimate")).thenReturn(originalEstimateNode);
        when(originalEstimateNode.isNull()).thenReturn(true);
        
        when(targetWorkItemNode.get("RemainingWork")).thenReturn(remainingWorkNode);
        when(remainingWorkNode.isNull()).thenReturn(true);
        
        when(targetWorkItemNode.get("CompletedWork")).thenReturn(completedWorkNode);
        when(completedWorkNode.isNull()).thenReturn(true);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(201, item.getWorkItemId());
        assertEquals("Task 201", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(null, item.getOriginalEstimate());
        assertEquals(null, item.getRemainingWork());
        assertEquals(0.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithNullOriginalEstimate() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ { \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 202, \"Title\": \"Task 202\", \"State\": \"Active\", \"OriginalEstimate\": null, \"RemainingWork\": 4.0, \"CompletedWork\": 1.0 } } ] } ] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode = mock(JsonNode.class);
        JsonNode linksNode = mock(JsonNode.class);
        JsonNode linkNode = mock(JsonNode.class);
        JsonNode targetWorkItemNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode).iterator());
        when(itemNode.get("Links")).thenReturn(linksNode);
        when(linksNode.iterator()).thenReturn(List.of(linkNode).iterator());
        when(linkNode.get("TargetWorkItem")).thenReturn(targetWorkItemNode);
        
        JsonNode workItemIdNode = mock(JsonNode.class);
        JsonNode titleNode = mock(JsonNode.class);
        JsonNode stateNode = mock(JsonNode.class);
        JsonNode originalEstimateNode = mock(JsonNode.class);
        JsonNode remainingWorkNode = mock(JsonNode.class);
        JsonNode completedWorkNode = mock(JsonNode.class);
        
        when(targetWorkItemNode.get("WorkItemId")).thenReturn(workItemIdNode);
        when(workItemIdNode.asInt()).thenReturn(202);
        
        when(targetWorkItemNode.get("Title")).thenReturn(titleNode);
        when(titleNode.asText()).thenReturn("Task 202");
        
        when(targetWorkItemNode.get("State")).thenReturn(stateNode);
        when(stateNode.asText()).thenReturn("Active");
        
        when(targetWorkItemNode.get("OriginalEstimate")).thenReturn(originalEstimateNode);
        when(originalEstimateNode.isNull()).thenReturn(true);
        
        when(targetWorkItemNode.get("RemainingWork")).thenReturn(remainingWorkNode);
        when(remainingWorkNode.isNull()).thenReturn(false);
        when(remainingWorkNode.asDouble()).thenReturn(4.0);
        
        when(targetWorkItemNode.get("CompletedWork")).thenReturn(completedWorkNode);
        when(completedWorkNode.isNull()).thenReturn(false);
        when(completedWorkNode.asDouble()).thenReturn(1.0);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(202, item.getWorkItemId());
        assertEquals("Task 202", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(null, item.getOriginalEstimate());
        assertEquals(4.0, item.getRemainingWork());
        assertEquals(1.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithNullRemainingWork() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ { \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 203, \"Title\": \"Task 203\", \"State\": \"Active\", \"OriginalEstimate\": 6.0, \"RemainingWork\": null, \"CompletedWork\": 2.0 } } ] } ] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode = mock(JsonNode.class);
        JsonNode linksNode = mock(JsonNode.class);
        JsonNode linkNode = mock(JsonNode.class);
        JsonNode targetWorkItemNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode).iterator());
        when(itemNode.get("Links")).thenReturn(linksNode);
        when(linksNode.iterator()).thenReturn(List.of(linkNode).iterator());
        when(linkNode.get("TargetWorkItem")).thenReturn(targetWorkItemNode);
        
        JsonNode workItemIdNode = mock(JsonNode.class);
        JsonNode titleNode = mock(JsonNode.class);
        JsonNode stateNode = mock(JsonNode.class);
        JsonNode originalEstimateNode = mock(JsonNode.class);
        JsonNode remainingWorkNode = mock(JsonNode.class);
        JsonNode completedWorkNode = mock(JsonNode.class);
        
        when(targetWorkItemNode.get("WorkItemId")).thenReturn(workItemIdNode);
        when(workItemIdNode.asInt()).thenReturn(203);
        
        when(targetWorkItemNode.get("Title")).thenReturn(titleNode);
        when(titleNode.asText()).thenReturn("Task 203");
        
        when(targetWorkItemNode.get("State")).thenReturn(stateNode);
        when(stateNode.asText()).thenReturn("Active");
        
        when(targetWorkItemNode.get("OriginalEstimate")).thenReturn(originalEstimateNode);
        when(originalEstimateNode.isNull()).thenReturn(false);
        when(originalEstimateNode.asDouble()).thenReturn(6.0);
        
        when(targetWorkItemNode.get("RemainingWork")).thenReturn(remainingWorkNode);
        when(remainingWorkNode.isNull()).thenReturn(true);
        
        when(targetWorkItemNode.get("CompletedWork")).thenReturn(completedWorkNode);
        when(completedWorkNode.isNull()).thenReturn(false);
        when(completedWorkNode.asDouble()).thenReturn(2.0);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(203, item.getWorkItemId());
        assertEquals("Task 203", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(6.0, item.getOriginalEstimate());
        assertEquals(null, item.getRemainingWork());
        assertEquals(2.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithNullCompletedWork() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ { \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 204, \"Title\": \"Task 204\", \"State\": \"Active\", \"OriginalEstimate\": 7.0, \"RemainingWork\": 3.0, \"CompletedWork\": null } } ] } ] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode valueNode = mock(JsonNode.class);
        JsonNode itemNode = mock(JsonNode.class);
        JsonNode linksNode = mock(JsonNode.class);
        JsonNode linkNode = mock(JsonNode.class);
        JsonNode targetWorkItemNode = mock(JsonNode.class);
        
        when(objectMapper.readTree(jsonResponse)).thenReturn(rootNode);
        when(rootNode.get("value")).thenReturn(valueNode);
        when(valueNode.iterator()).thenReturn(List.of(itemNode).iterator());
        when(itemNode.get("Links")).thenReturn(linksNode);
        when(linksNode.iterator()).thenReturn(List.of(linkNode).iterator());
        when(linkNode.get("TargetWorkItem")).thenReturn(targetWorkItemNode);
        
        JsonNode workItemIdNode = mock(JsonNode.class);
        JsonNode titleNode = mock(JsonNode.class);
        JsonNode stateNode = mock(JsonNode.class);
        JsonNode originalEstimateNode = mock(JsonNode.class);
        JsonNode remainingWorkNode = mock(JsonNode.class);
        JsonNode completedWorkNode = mock(JsonNode.class);
        
        when(targetWorkItemNode.get("WorkItemId")).thenReturn(workItemIdNode);
        when(workItemIdNode.asInt()).thenReturn(204);
        
        when(targetWorkItemNode.get("Title")).thenReturn(titleNode);
        when(titleNode.asText()).thenReturn("Task 204");
        
        when(targetWorkItemNode.get("State")).thenReturn(stateNode);
        when(stateNode.asText()).thenReturn("Active");
        
        when(targetWorkItemNode.get("OriginalEstimate")).thenReturn(originalEstimateNode);
        when(originalEstimateNode.isNull()).thenReturn(false);
        when(originalEstimateNode.asDouble()).thenReturn(7.0);
        
        when(targetWorkItemNode.get("RemainingWork")).thenReturn(remainingWorkNode);
        when(remainingWorkNode.isNull()).thenReturn(false);
        when(remainingWorkNode.asDouble()).thenReturn(3.0);
        
        when(targetWorkItemNode.get("CompletedWork")).thenReturn(completedWorkNode);
        when(completedWorkNode.isNull()).thenReturn(true);
        
        List<TargetWorkItem> result = workItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(204, item.getWorkItemId());
        assertEquals("Task 204", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(7.0, item.getOriginalEstimate());
        assertEquals(3.0, item.getRemainingWork());
        assertEquals(0.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithRealJson() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
                "{ \"Links\": [ { \"TargetWorkItem\": { \"WorkItemId\": 205, \"Title\": \"Task 205\", \"State\": \"Active\", \"OriginalEstimate\": 9.0, \"RemainingWork\": 4.5, \"CompletedWork\": null } } ] }" +
                "] }";
        UserStoryRequest request = new UserStoryRequest(jsonResponse, null, jsonResponse, false);
        request.setConcluded(false);
        
        MockitoAnnotations.openMocks(this);
        WorkItemService realWorkItemService = new WorkItemService(new ObjectMapper());
        
        List<TargetWorkItem> result = realWorkItemService.processAzureDevOpsResponse(jsonResponse, request);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(205, item.getWorkItemId());
        assertEquals("Task 205", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(9.0, item.getOriginalEstimate());
        assertEquals(4.5, item.getRemainingWork());
        assertEquals(0.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponse_WithAllFieldsPresent() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
            "{ \"Links\": [ { \"TargetWorkItem\": { " +
                "\"WorkItemId\": 301, " +
                "\"Title\": \"Task 301\", " +
                "\"State\": \"Active\", " +
                "\"OriginalEstimate\": 10.0, " +
                "\"RemainingWork\": 5.0, " +
                "\"CompletedWork\": 5.0 " +
            "} } ] }" +
        "] }";
        
        WorkItemService realWorkItemService = new WorkItemService(new ObjectMapper());
        
        List<TargetWorkItem> result = realWorkItemService.processAzureDevOpsResponse(jsonResponse);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(301, item.getWorkItemId());
        assertEquals("Task 301", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(10.0, item.getOriginalEstimate());
        assertEquals(5.0, item.getRemainingWork());
        assertEquals(5.0, item.getCompletedWork());
    }
    
    @Test
    void testProcessAzureDevOpsResponseString_WithNullOriginalEstimate() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
            "{ \"Links\": [ { \"TargetWorkItem\": { " +
                "\"WorkItemId\": 302, " +
                "\"Title\": \"Task 302\", " +
                "\"State\": \"Active\", " +
                "\"OriginalEstimate\": null, " +
                "\"RemainingWork\": 4.0, " +
                "\"CompletedWork\": 2.0 " +
            "} } ] }" +
        "] }";
        
        WorkItemService realWorkItemService = new WorkItemService(new ObjectMapper());

        List<TargetWorkItem> result = realWorkItemService.processAzureDevOpsResponse(jsonResponse);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(302, item.getWorkItemId());
        assertEquals("Task 302", item.getTitle());
        assertEquals("Active", item.getState());
        assertNull(item.getOriginalEstimate());
        assertEquals(4.0, item.getRemainingWork());
        assertEquals(2.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponseString_WithNullRemainingWork() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
            "{ \"Links\": [ { \"TargetWorkItem\": { " +
                "\"WorkItemId\": 303, " +
                "\"Title\": \"Task 303\", " +
                "\"State\": \"Active\", " +
                "\"OriginalEstimate\": 8.0, " +
                "\"RemainingWork\": null, " +
                "\"CompletedWork\": 3.0 " +
            "} } ] }" +
        "] }";
        
        WorkItemService realWorkItemService = new WorkItemService(new ObjectMapper());
        
        List<TargetWorkItem> result = realWorkItemService.processAzureDevOpsResponse(jsonResponse);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(303, item.getWorkItemId());
        assertEquals("Task 303", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(8.0, item.getOriginalEstimate());
        assertNull(item.getRemainingWork());
        assertEquals(3.0, item.getCompletedWork());
    }

    @Test
    void testProcessAzureDevOpsResponseString_WithNullCompletedWork() throws JsonProcessingException {
        String jsonResponse = "{ \"value\": [ " +
            "{ \"Links\": [ { \"TargetWorkItem\": { " +
                "\"WorkItemId\": 304, " +
                "\"Title\": \"Task 304\", " +
                "\"State\": \"Active\", " +
                "\"OriginalEstimate\": 12.0, " +
                "\"RemainingWork\": 6.0, " +
                "\"CompletedWork\": null " +
            "} } ] }" +
        "] }";
        
        WorkItemService realWorkItemService = new WorkItemService(new ObjectMapper());
        
        List<TargetWorkItem> result = realWorkItemService.processAzureDevOpsResponse(jsonResponse);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TargetWorkItem item = result.get(0);
        assertEquals(304, item.getWorkItemId());
        assertEquals("Task 304", item.getTitle());
        assertEquals("Active", item.getState());
        assertEquals(12.0, item.getOriginalEstimate());
        assertEquals(6.0, item.getRemainingWork());
        assertEquals(0.0, item.getCompletedWork());
    }
}