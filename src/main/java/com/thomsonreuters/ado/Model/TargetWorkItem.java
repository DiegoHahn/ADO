package com.thomsonreuters.ado.Model;

public class TargetWorkItem {
    private int workItemId;
    private String assignedToUserSK;
    private String title;
    private String state;
    private Double originalEstimate;
    private Double remainingWork;
    private Double completedWork;

    public TargetWorkItem(int workItemId, String assignedToUserSK, String title, String state, Double originalEstimate, Double remainingWork, Double completedWork) {
        this.workItemId = workItemId;
        this.assignedToUserSK = assignedToUserSK;
        this.title = title;
        this.state = state;
        this.originalEstimate = originalEstimate;
        this.remainingWork = remainingWork;
        this.completedWork = completedWork;
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

    public Double getCompletedWork() {
        return completedWork;
    }

    public void setCompletedWork(Double completedWork) {
        this.completedWork = completedWork;
    }

    @Override
    public String toString() {
        return "TargetWorkItem{" +
                "workItemId=" + workItemId +
                ", assignedToUserSK='" + assignedToUserSK + '\'' +
                ", title='" + title + '\'' +
                ", state='" + state + '\'' +
                ", originalEstimate=" + originalEstimate +
                ", remainingWork=" + remainingWork +
                ", completedWork=" + completedWork +
                '}';
    }
}