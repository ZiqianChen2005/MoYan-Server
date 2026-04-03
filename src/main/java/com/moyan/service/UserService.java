package com.moyan.service;

import com.moyan.dto.Response;
import com.moyan.entity.User;

public interface UserService {
    Response<User> login(String phone, String code);
    Response<User> register(String phone, String nickname);
    Response<User> getUserInfo(Integer userId);
    Response<Void> updateNickname(Integer userId, String nickname);
    Response<Void> updateAvatar(Integer userId, String avatarUrl);
    Response<Void> addWarning(Integer userId);
    Response<Void> banUser(Integer userId);
    Response<Void> unbanUser(Integer userId);
}