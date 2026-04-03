package com.moyan.entity;

import java.util.Date;

public class Reply {
    private Integer replyId;
    private Integer postId;
    private Integer userId;
    private Boolean isAnonymous;
    private Integer anonymousNum;
    private String content;
    private Date replyTime;
    private Integer status;    // 0待审核 1已通过 2已拒绝
    
    // 关联字段
    private String authorNickname;
    private String postTitle;

    public Reply() {}

    // Getters and Setters
    public Integer getReplyId() { return replyId; }
    public void setReplyId(Integer replyId) { this.replyId = replyId; }
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public Integer getAnonymousNum() { return anonymousNum; }
    public void setAnonymousNum(Integer anonymousNum) { this.anonymousNum = anonymousNum; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getReplyTime() { return replyTime; }
    public void setReplyTime(Date replyTime) { this.replyTime = replyTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }
}