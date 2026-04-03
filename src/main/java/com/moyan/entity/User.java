package com.moyan.entity;

import java.util.Date;

public class User {
    private Integer userId;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String passwordHash;
    private Boolean isVip;
    private Date vipExpireDate;
    private Integer warningCount;
    private Boolean isBanned;
    private Date registerTime;
    private Date lastLoginTime;

    public User() {}

    public User(Integer userId, String phone, String nickname, String avatarUrl, 
                String passwordHash, Boolean isVip, Date vipExpireDate, 
                Integer warningCount, Boolean isBanned, Date registerTime, Date lastLoginTime) {
        this.userId = userId;
        this.phone = phone;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.passwordHash = passwordHash;
        this.isVip = isVip;
        this.vipExpireDate = vipExpireDate;
        this.warningCount = warningCount;
        this.isBanned = isBanned;
        this.registerTime = registerTime;
        this.lastLoginTime = lastLoginTime;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Boolean getIsVip() { return isVip; }
    public void setIsVip(Boolean isVip) { this.isVip = isVip; }
    public Date getVipExpireDate() { return vipExpireDate; }
    public void setVipExpireDate(Date vipExpireDate) { this.vipExpireDate = vipExpireDate; }
    public Integer getWarningCount() { return warningCount; }
    public void setWarningCount(Integer warningCount) { this.warningCount = warningCount; }
    public Boolean getIsBanned() { return isBanned; }
    public void setIsBanned(Boolean isBanned) { this.isBanned = isBanned; }
    public Date getRegisterTime() { return registerTime; }
    public void setRegisterTime(Date registerTime) { this.registerTime = registerTime; }
    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", phone='" + phone + "', nickname='" + nickname + "'}";
    }
}