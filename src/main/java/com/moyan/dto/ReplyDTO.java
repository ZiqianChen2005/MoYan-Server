package com.moyan.dto;

import java.util.Date;

public class ReplyDTO {
    private Integer replyId;
    private String authorName;
    private Boolean isAnonymous;
    private Integer anonymousNum;
    private String content;
    private Date replyTime;

    // Getters and Setters
    public Integer getReplyId() { return replyId; }
    public void setReplyId(Integer replyId) { this.replyId = replyId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public Integer getAnonymousNum() { return anonymousNum; }
    public void setAnonymousNum(Integer anonymousNum) { this.anonymousNum = anonymousNum; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getReplyTime() { return replyTime; }
    public void setReplyTime(Date replyTime) { this.replyTime = replyTime; }
}