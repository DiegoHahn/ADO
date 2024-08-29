package com.thomsonreuters.ado.service;

public class AzureDevOpsService {

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
