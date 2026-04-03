package com.moyan.service.impl;

import com.moyan.dao.UserDao;
import com.moyan.dao.impl.UserDaoImpl;
import com.moyan.dto.Response;
import com.moyan.entity.User;
import com.moyan.service.UserService;

public class UserServiceImpl implements UserService {
    
    private UserDao userDao = new UserDaoImpl();
    
    @Override
    public Response<User> login(String phone, String code) {
        // 测试阶段验证码固定123456
        if (!"123456".equals(code)) {
            return Response.fail("验证码错误");
        }
        
        User user = userDao.findByPhone(phone);
        if (user == null) {
            return Response.fail("用户不存在");
        }
        
        if (user.getIsBanned()) {
            return Response.fail("账号已被封禁");
        }
        
        userDao.updateLastLoginTime(user.getUserId());
        return Response.success(user);
    }
    
    @Override
    public Response<User> register(String phone, String nickname) {
        // 检查手机号是否已注册
        User existUser = userDao.findByPhone(phone);
        if (existUser != null) {
            return Response.fail("手机号已注册");
        }
        
        // 检查昵称是否已存在
        existUser = userDao.findByNickname(nickname);
        if (existUser != null) {
            return Response.fail("昵称已被使用");
        }
        
        User user = new User();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setIsVip(false);
        
        int userId = userDao.insert(user);
        if (userId > 0) {
            user.setUserId(userId);
            return Response.success(user);
        }
        
        return Response.fail("注册失败");
    }
    
    @Override
    public Response<User> getUserInfo(Integer userId) {
        User user = userDao.findByUserId(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }
        return Response.success(user);
    }
    
    @Override
    public Response<Void> updateNickname(Integer userId, String nickname) {
        User existUser = userDao.findByNickname(nickname);
        if (existUser != null && !existUser.getUserId().equals(userId)) {
            return Response.fail("昵称已被使用");
        }
        
        int result = userDao.updateNickname(userId, nickname);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("修改失败");
    }
    
    @Override
    public Response<Void> updateAvatar(Integer userId, String avatarUrl) {
        int result = userDao.updateAvatar(userId, avatarUrl);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("修改失败");
    }
    
    @Override
    public Response<Void> addWarning(Integer userId) {
        int result = userDao.addWarningCount(userId);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("操作失败");
    }
    
    @Override
    public Response<Void> banUser(Integer userId) {
        int result = userDao.banUser(userId);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("封禁失败");
    }
    
    @Override
    public Response<Void> unbanUser(Integer userId) {
        int result = userDao.unbanUser(userId);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("解封失败");
    }
}