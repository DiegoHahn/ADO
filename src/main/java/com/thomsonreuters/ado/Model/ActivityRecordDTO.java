package com.thomsonreuters.ado.Model;

public class ActivityRecordDTO {
    private String board;
    private String userStoryId;
    private boolean concluded;
    private TargetWorkItem task;
    private Double originalEstimate;
    private Double remainingWork;
    private String startTime;
    private String completedWork;

    public ActivityRecordDTO(String board, String userStoryId, boolean concluded, TargetWorkItem task, Double originalEstimate, Double remainingWork, String startTime, String completedWork) {
        this.board = board;
        this.userStoryId = userStoryId;
        this.concluded = concluded;
        this.task = task;
        this.originalEstimate = originalEstimate;
        this.remainingWork = remainingWork;
        this.startTime = startTime;
        this.completedWork = completedWork;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public boolean isConcluded() {
        return concluded;
    }

    public void setConcluded(boolean concluded) {
        this.concluded = concluded;
    }

    public TargetWorkItem getTask() {
        return task;
    }

    public void setTask(TargetWorkItem task) {
        this.task = task;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCompletedWork() {
        return completedWork;
    }

    public void setCompletedWork(String completedWork) {
        this.completedWork = completedWork;
    }
}