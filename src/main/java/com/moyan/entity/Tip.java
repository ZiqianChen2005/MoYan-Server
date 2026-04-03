package com.moyan.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Tip {
    private Integer tipId;
    private Integer fromUserId;
    private Integer toUserId;
    private Integer postId;
    private BigDecimal amount;
    private BigDecimal platformFee;
    private BigDecimal authorIncome;
    private Date tipTime;

    public Tip() {}

    // Getters and Setters
    public Integer getTipId() { return tipId; }
    public void setTipId(Integer tipId) { this.tipId = tipId; }
    public Integer getFromUserId() { return fromUserId; }
    public void setFromUserId(Integer fromUserId) { this.fromUserId = fromUserId; }
    public Integer getToUserId() { return toUserId; }
    public void setToUserId(Integer toUserId) { this.toUserId = toUserId; }
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }
    public BigDecimal getAuthorIncome() { return authorIncome; }
    public void setAuthorIncome(BigDecimal authorIncome) { this.authorIncome = authorIncome; }
    public Date getTipTime() { return tipTime; }
    public void setTipTime(Date tipTime) { this.tipTime = tipTime; }
}