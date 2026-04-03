package com.moyan.dto;

import java.util.Date;
import java.util.List;

public class PostDetailDTO {
    private Integer postId;
    private String title;
    private String content;
    private String tags;
    private String authorName;
    private Boolean isAnonymous;
    private Date postTime;
    private Double totalScore;
    private Integer viewCount;
    private Integer replyCount;
    private Boolean isNewbie;
    private Boolean canUserRate;      // 当前用户是否已评分
    private Integer userRatingTag;    // 用户评的tag准确度
    private Integer userRatingArticle; // 用户评的文章分数
    private List<ReplyDTO> replies;

    // Getters and Setters
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public Date getPostTime() { return postTime; }
    public void setPostTime(Date postTime) { this.postTime = postTime; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    public Boolean getIsNewbie() { return isNewbie; }
    public void setIsNewbie(Boolean isNewbie) { this.isNewbie = isNewbie; }
    public Boolean getCanUserRate() { return canUserRate; }
    public void setCanUserRate(Boolean canUserRate) { this.canUserRate = canUserRate; }
    public Integer getUserRatingTag() { return userRatingTag; }
    public void setUserRatingTag(Integer userRatingTag) { this.userRatingTag = userRatingTag; }
    public Integer getUserRatingArticle() { return userRatingArticle; }
    public void setUserRatingArticle(Integer userRatingArticle) { this.userRatingArticle = userRatingArticle; }
    public List<ReplyDTO> getReplies() { return replies; }
    public void setReplies(List<ReplyDTO> replies) { this.replies = replies; }
}