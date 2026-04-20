package com.moyan.service.impl;

import com.moyan.dao.UserDao;
import com.moyan.dao.impl.UserDaoImpl;
import com.moyan.dto.Response;
import com.moyan.entity.User;
import com.moyan.service.UserService;

public class UserServiceImpl implements UserService {
    
    private UserDao userDao = new UserDaoImpl();
    
    // 修改：使用密码登录，不再使用验证码
    @Override
    public Response<User> login(String phone, String password) {
        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            return Response.fail("手机号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.fail("密码不能为空");
        }
        
        // 根据手机号和密码查询用户
        User user = userDao.findByPhoneAndPassword(phone, password);
        if (user == null) {
            return Response.fail("手机号或密码错误");
        }
        
        // 检查是否被封禁
        if (user.getIsBanned()) {
            return Response.fail("账号已被封禁，请联系管理员");
        }
        
        // 更新最后登录时间
        userDao.updateLastLoginTime(user.getUserId());
        
        // 返回用户信息（清除敏感字段）
        user.setPasswordHash(null);
        
        return Response.success("登录成功", user);
    }
    
    // 修改：注册时需要密码
    @Override
    public Response<User> register(String phone, String password, String nickname) {
        // 参数验证
        if (phone == null || phone.trim().isEmpty()) {
            return Response.fail("手机号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.fail("密码不能为空");
        }
        if (password.length() < 6) {
            return Response.fail("密码长度不能少于6位");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            return Response.fail("昵称不能为空");
        }
        
        // 检查手机号是否已注册
        if (userDao.existsByPhone(phone)) {
            return Response.fail("该手机号已注册");
        }
        
        // 检查昵称是否已存在
        if (userDao.findByNickname(nickname) != null) {
            return Response.fail("该昵称已被使用");
        }
        
        // 创建新用户
        User newUser = new User();
        newUser.setPhone(phone);
        newUser.setPasswordHash(password);  // DAO层会进行MD5加密
        newUser.setNickname(nickname);
        newUser.setAvatarUrl("");  // 默认头像
        
        int userId = userDao.insert(newUser);
        if (userId > 0) {
            newUser.setUserId(userId);
            newUser.setPasswordHash(null);  // 清除密码字段
            return Response.success("注册成功", newUser);
        }
        
        return Response.fail("注册失败，请稍后重试");
    }
    
    @Override
    public Response<User> getUserInfo(Integer userId) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        
        User user = userDao.findByUserId(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }
        
        user.setPasswordHash(null);  // 清除敏感信息
        return Response.success(user);
    }
    
    @Override
    public Response<Void> updateNickname(Integer userId, String nickname) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            return Response.fail("昵称不能为空");
        }
        
        // 检查昵称是否已被其他用户使用
        User existUser = userDao.findByNickname(nickname);
        if (existUser != null && !existUser.getUserId().equals(userId)) {
            return Response.fail("该昵称已被使用");
        }
        
        int result = userDao.updateNickname(userId, nickname);
        if (result > 0) {
            return Response.success("修改成功", null);
        }
        return Response.fail("修改失败");
    }
    
    @Override
    public Response<Void> updateAvatar(Integer userId, String avatarUrl) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return Response.fail("头像地址不能为空");
        }
        
        int result = userDao.updateAvatar(userId, avatarUrl);
        if (result > 0) {
            return Response.success("修改成功", null);
        }
        return Response.fail("修改失败");
    }
    
    // 新增：修改密码
    @Override
    public Response<Void> updatePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return Response.fail("原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Response.fail("新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return Response.fail("新密码长度不能少于6位");
        }
        
        // 验证原密码是否正确
        User user = userDao.findByUserId(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }
        
        // 使用MD5比较原密码
        User verifyUser = userDao.findByPhoneAndPassword(user.getPhone(), oldPassword);
        if (verifyUser == null) {
            return Response.fail("原密码错误");
        }
        
        // 更新密码
        int result = userDao.updatePassword(userId, newPassword);
        if (result > 0) {
            return Response.success("密码修改成功", null);
        }
        return Response.fail("密码修改失败");
    }
    
    @Override
    public Response<Void> addWarning(Integer userId) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        
        int result = userDao.addWarningCount(userId);
        if (result > 0) {
            return Response.success("警告成功", null);
        }
        return Response.fail("警告失败");
    }
    
    @Override
    public Response<Void> banUser(Integer userId) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        
        int result = userDao.banUser(userId);
        if (result > 0) {
            return Response.success("封禁成功", null);
        }
        return Response.fail("封禁失败");
    }
    
    @Override
    public Response<Void> unbanUser(Integer userId) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        
        int result = userDao.unbanUser(userId);
        if (result > 0) {
            return Response.success("解封成功", null);
        }
        return Response.fail("解封失败");
    }
}