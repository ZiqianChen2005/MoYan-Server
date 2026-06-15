package com.moyan.dto;

import java.util.Date;

public class ReplyDTO {
    private Integer replyId;
    private Integer postId;        // 所属帖子ID
    private String postTitle;      // 所属帖子标题
    private Integer userId;        // 回复作者ID
    private String authorName;
    private Boolean isAnonymous;
    private Integer anonymousNum;
    private String content;
    private Date replyTime;
    private Integer status;        // 回复状态：0待审核 1已通过 2已拒绝

    // Getters and Setters
    public Integer getReplyId() { return replyId; }
    public void setReplyId(Integer replyId) { this.replyId = replyId; }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}