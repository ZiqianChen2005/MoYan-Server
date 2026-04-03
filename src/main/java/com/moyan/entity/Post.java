package com.moyan.entity;

import java.util.Date;

public class Post {
    private Integer postId;
    private Integer userId;
    private Boolean isAnonymous;
    private String title;
    private String content;
    private String tags;
    private Date postTime;
    private Boolean isNewbie;
    private Integer status;    // 0待审核 1已通过 2已拒绝 3已举报待处理
    private Integer viewCount;
    
    // 关联字段（非数据库字段）
    private String authorNickname;
    private Double totalScore;  // 综合评分

    public Post() {}

    // Getters and Setters
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Date getPostTime() { return postTime; }
    public void setPostTime(Date postTime) { this.postTime = postTime; }
    public Boolean getIsNewbie() { return isNewbie; }
    public void setIsNewbie(Boolean isNewbie) { this.isNewbie = isNewbie; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
}