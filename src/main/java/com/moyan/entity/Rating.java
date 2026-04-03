package com.moyan.entity;

import java.util.Date;

public class Rating {
    private Integer ratingId;
    private Integer postId;
    private Integer userId;
    private Integer tagAccuracy;   // 1-5
    private Integer articleScore;  // 1-5
    private String comment;
    private Date ratingTime;

    public Rating() {}

    // Getters and Setters
    public Integer getRatingId() { return ratingId; }
    public void setRatingId(Integer ratingId) { this.ratingId = ratingId; }
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getTagAccuracy() { return tagAccuracy; }
    public void setTagAccuracy(Integer tagAccuracy) { this.tagAccuracy = tagAccuracy; }
    public Integer getArticleScore() { return articleScore; }
    public void setArticleScore(Integer articleScore) { this.articleScore = articleScore; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getRatingTime() { return ratingTime; }
    public void setRatingTime(Date ratingTime) { this.ratingTime = ratingTime; }
}