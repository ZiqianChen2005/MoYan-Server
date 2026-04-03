package com.moyan.dao;

import com.moyan.entity.User;
import java.util.List;

public interface UserDao {
    User findByUserId(Integer userId);
    User findByPhone(String phone);
    User findByNickname(String nickname);
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
}