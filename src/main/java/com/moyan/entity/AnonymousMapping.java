package com.moyan.entity;

public class AnonymousMapping {
    private Integer mappingId;
    private Integer postId;
    private Integer userId;
    private Integer anonymousNum;

    public AnonymousMapping() {}

    // Getters and Setters
    public Integer getMappingId() { return mappingId; }
    public void setMappingId(Integer mappingId) { this.mappingId = mappingId; }
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getAnonymousNum() { return anonymousNum; }
    public void setAnonymousNum(Integer anonymousNum) { this.anonymousNum = anonymousNum; }
}