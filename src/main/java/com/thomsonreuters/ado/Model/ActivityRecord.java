package com.thomsonreuters.ado.Model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activity_records")
public class ActivityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String board;
    @Column(name = "user_story_id")
    private String userStoryId;
    private boolean concluded;

    @Column(name = "work_item_id")
    private int workItemId;

    @Column(name = "title")
    private String title;
    private String state;

    @Column(name = "original_estimate")
    private Double originalEstimate;

    @Column(name = "remaining_work")
    private Double remainingWork;
    private OffsetDateTime startTime;

    @Column(name = "completed_work")
    private String completedWork;

    @Column(name = "currente_tracked_time")
    private String currentTrackedTime;

    @Column
    private int status;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserInformation userId;

    public ActivityRecord() {
    }

    public ActivityRecord(Long id, String board, String userStoryId, boolean concluded, int workItemId, String title, String state, Double originalEstimate, Double remainingWork, OffsetDateTime startTime, String completedWork, String currentTrackedTime, int status, UserInformation userId) {
        this.id = id;
        this.board = board;
        this.userStoryId = userStoryId;
        this.concluded = concluded;
        this.workItemId = workItemId;
        this.title = title;
        this.state = state;
        this.originalEstimate = originalEstimate;
        this.remainingWork = remainingWork;
        this.startTime = startTime;
        this.completedWork = completedWork;
        this.currentTrackedTime = currentTrackedTime;
        this.status = status;
        this.userId = userId;
    }

    public ActivityRecord(Long id, String board, String userStoryId, boolean concluded, int workItemId, String assignedToUserSK, String title, String state, Double originalEstimate, Double remainingWork, LocalTime parse, String completedWork, String currentTrackedTime) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(int workItemId) {
        this.workItemId = workItemId;
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

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public String getCompletedWork() {
        return completedWork;
    }

    public void setCompletedWork(String completedWork) {
        this.completedWork = completedWork;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserInformation getUserId() {
        return userId;
    }

    public void setUserId(UserInformation user) {
        this.userId = user;
    }

    public String getCurrentTrackedTime() {
        return currentTrackedTime;
    }

    public void setCurrentTrackedTime(String currentTrackedTime) {
        this.currentTrackedTime = currentTrackedTime;
    }
}
