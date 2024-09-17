package com.thomsonreuters.ado.Model;

public class TargetWorkItem {
    private int workItemId;
    private String assignedToUserSK;
    private String title;
    private String state;
    private Double originalEstimate;
    private Double remainingWork;

    public TargetWorkItem(int workItemId, String assignedToUserSK, String title, String state, Double originalEstimate, Double remainingWork) {
        this.workItemId = workItemId;
        this.assignedToUserSK = assignedToUserSK;
        this.title = title;
        this.state = state;
        this.originalEstimate = originalEstimate;
        this.remainingWork = remainingWork;
    }

    public int getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(int workItemId) {
        this.workItemId = workItemId;
    }

    public String getAssignedToUserSK() {
        return assignedToUserSK;
    }

    public void setAssignedToUserSK(String assignedToUserSK) {
        this.assignedToUserSK = assignedToUserSK;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getOriginalEstimate() {
        return originalEstimate;
    }

    public void setOriginalEstimate(Double originalEstimate) {
        this.originalEstimate = originalEstimate;
    }

    public Double getRemainingWork() {
        return remainingWork;
    }

    public void setRemainingWork(Double remainingWork) {
        this.remainingWork = remainingWork;
    }
}