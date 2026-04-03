package com.moyan.entity;

import java.util.Date;

public class Report {
    private Integer reportId;
    private Integer reporterId;
    private Integer targetType;   // 1帖子 2回复
    private Integer targetId;
    private String reason;
    private Date reportTime;
    private Integer status;       // 0待处理 1已处理(撤下) 2驳回
    private Integer handlerId;
    private Date handleTime;
    private String handleNote;

    public Report() {}

    // Getters and Setters
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }
    public Integer getReporterId() { return reporterId; }
    public void setReporterId(Integer reporterId) { this.reporterId = reporterId; }
    public Integer getTargetType() { return targetType; }
    public void setTargetType(Integer targetType) { this.targetType = targetType; }
    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Date getReportTime() { return reportTime; }
    public void setReportTime(Date reportTime) { this.reportTime = reportTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getHandlerId() { return handlerId; }
    public void setHandlerId(Integer handlerId) { this.handlerId = handlerId; }
    public Date getHandleTime() { return handleTime; }
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }
    public String getHandleNote() { return handleNote; }
    public void setHandleNote(String handleNote) { this.handleNote = handleNote; }
}