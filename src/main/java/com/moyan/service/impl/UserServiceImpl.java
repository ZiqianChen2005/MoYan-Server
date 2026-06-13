package com.moyan.service.impl;

import com.moyan.dao.UserDao;
import com.moyan.dao.impl.UserDaoImpl;
import com.moyan.dto.Response;
import com.moyan.entity.User;
import com.moyan.service.UserService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao = new UserDaoImpl();

    @Override
    public Response<User> login(String phone, String password) {
        if (phone == null || phone.trim().isEmpty()) {
            return Response.fail("手机号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.fail("密码不能为空");
        }

        User user = userDao.findByPhoneAndPassword(phone, password);
        if (user == null) {
            return Response.fail("手机号或密码错误");
        }

        if (user.getIsBanned()) {
            return Response.fail("账号已被封禁，请联系管理员");
        }

        userDao.updateLastLoginTime(user.getUserId());

        String token = generateToken();
        Date tokenExpireTime = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
        userDao.updateToken(user.getUserId(), token, tokenExpireTime);

        user.setToken(token);
        user.setTokenExpireTime(tokenExpireTime);
        user.setPasswordHash(null);

        return Response.success("登录成功", user);
    }

    @Override
    public Response<User> register(String phone, String password, String nickname) {
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

        if (userDao.existsByPhone(phone)) {
            return Response.fail("该手机号已注册");
        }

        if (userDao.findByNickname(nickname) != null) {
            return Response.fail("该昵称已被使用");
        }

        User newUser = new User();
        newUser.setPhone(phone);
        newUser.setPasswordHash(password);
        newUser.setNickname(nickname);
        newUser.setAvatarUrl("");

        int userId = userDao.insert(newUser);
        if (userId > 0) {
            newUser.setUserId(userId);
            newUser.setPasswordHash(null);
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

        user.setPasswordHash(null);
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

        User user = userDao.findByUserId(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }

        User verifyUser = userDao.findByPhoneAndPassword(user.getPhone(), oldPassword);
        if (verifyUser == null) {
            return Response.fail("原密码错误");
        }

        int result = userDao.updatePassword(userId, newPassword);
        if (result > 0) {
            return Response.success("密码修改成功", null);
        }
        return Response.fail("密码修改失败");
    }

    @Override
    public Response<Void> logout(Integer userId) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }

        int result = userDao.clearToken(userId);
        if (result > 0) {
            return Response.success("退出登录成功", null);
        }
        return Response.fail("退出登录失败");
    }

    @Override
    public Response<User> verifyToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Response.fail("token不能为空");
        }

        User user = userDao.findByToken(token);
        if (user == null) {
            return Response.fail("token无效或已过期");
        }

        if (user.getIsBanned()) {
            return Response.fail("账号已被封禁");
        }

        user.setPasswordHash(null);
        return Response.success("token验证成功", user);
    }

    @Override
    public Response<Void> deleteAccount(Integer userId, String password) {
        if (userId == null) {
            return Response.fail("用户ID不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.fail("密码不能为空");
        }

        User user = userDao.findByUserId(userId);
        if (user == null) {
            return Response.fail("用户不存在");
        }

        User verifyUser = userDao.findByPhoneAndPassword(user.getPhone(), password);
        if (verifyUser == null) {
            return Response.fail("密码错误");
        }

        userDao.clearToken(userId);

        int result = userDao.deleteUser(userId);
        if (result > 0) {
            return Response.success("账号注销成功", null);
        }
        return Response.fail("账号注销失败");
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

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
