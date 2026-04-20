package com.moyan.dao;

import com.moyan.entity.User;
import java.util.List;

public interface UserDao {
    User findByUserId(Integer userId);
    User findByPhone(String phone);
    User findByNickname(String nickname);
    
    // 新增：根据手机号和密码查找用户（登录验证）
    User findByPhoneAndPassword(String phone, String passwordHash);
    
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
}