package com.moyan.dao;

import com.moyan.entity.User;
import java.util.List;

public interface UserDao {
    User findByUserId(Integer userId);
    User findByPhone(String phone);
    User findByNickname(String nickname);

    User findByPhoneAndPassword(String phone, String passwordHash);

    User findByToken(String token);

    int insert(User user);
    int update(User user);
    int updateLastLoginTime(Integer userId);
    int updateNickname(Integer userId, String nickname);
    int updateAvatar(Integer userId, String avatarUrl);
    int addWarningCount(Integer userId);
    int banUser(Integer userId);
    int unbanUser(Integer userId);
    List<User> findAll(int page, int size);
    int countAll();
    int updatePassword(Integer userId, String newPasswordHash);
    boolean existsByPhone(String phone);
    int deleteUser(Integer userId);
    int updateToken(Integer userId, String token, java.util.Date expireTime);
    int clearToken(Integer userId);
}
