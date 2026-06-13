package com.moyan.service;

import com.moyan.dto.Response;
import com.moyan.entity.User;

public interface UserService {
    Response<User> login(String phone, String password);

    Response<User> register(String phone, String password, String nickname);

    Response<User> getUserInfo(Integer userId);
    Response<Void> updateNickname(Integer userId, String nickname);
    Response<Void> updateAvatar(Integer userId, String avatarUrl);
    Response<Void> addWarning(Integer userId);
    Response<Void> banUser(Integer userId);
    Response<Void> unbanUser(Integer userId);
    Response<Void> updatePassword(Integer userId, String oldPassword, String newPassword);
    Response<Void> logout(Integer userId);
    Response<User> verifyToken(String token);
    Response<Void> deleteAccount(Integer userId, String password);
}
