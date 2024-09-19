package com.thomsonreuters.ado.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.ado.Model.TargetWorkItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkItemService {

	private final ObjectMapper objectMapper;

	public WorkItemService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public List<TargetWorkItem> processAzureDevOpsResponse(String jsonResponse) throws JsonProcessingException {
		JsonNode rootNode = objectMapper.readTree(jsonResponse);
		JsonNode valueNode = rootNode.get("value");

		List<TargetWorkItem> targetWorkItems = new ArrayList<>();

		for (JsonNode itemNode : valueNode) {
			JsonNode linksNode = itemNode.get("Links");
			for (JsonNode linkNode : linksNode) {
				JsonNode targetWorkItemNode = linkNode.get("TargetWorkItem");
				TargetWorkItem targetWorkItem = new TargetWorkItem(
						targetWorkItemNode.get("WorkItemId").asInt(),
						targetWorkItemNode.get("AssignedToUserSK").asText(),
						targetWorkItemNode.get("Title").asText(),
						targetWorkItemNode.get("State").asText(),
						targetWorkItemNode.get("OriginalEstimate").isNull() ? 0 : targetWorkItemNode.get("OriginalEstimate").asDouble(),
						targetWorkItemNode.get("RemainingWork").isNull() ? 0 : targetWorkItemNode.get("RemainingWork").asDouble()
				);
				targetWorkItems.add(targetWorkItem);
			}
		}

		return targetWorkItems;
	}
    public static String UpdateWorkItemQuery(int remainingWork, int completedWork) {
        return String.format("""
				[
					{
						"op": "add",
						"path": "/fields/Microsoft.VSTS.Scheduling.RemainingWork",
						"value": %d
					},
					{
						"op": "add",
						"path": "/fields/Microsoft.VSTS.Scheduling.CompletedWork",
						"value": %d
					}
				]""", remainingWork, completedWork);
    }
}
