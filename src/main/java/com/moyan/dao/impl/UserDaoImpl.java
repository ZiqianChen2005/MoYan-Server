package com.moyan.dao.impl;

import com.moyan.dao.UserDao;
import com.moyan.entity.User;
import com.moyan.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    // 新增：MD5加密工具方法
    private String md5(String str) {
        if (str == null) return null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    @Override
    public User findByUserId(Integer userId) {
        String sql = "SELECT user_id, phone, nickname, avatar_url, password_hash, " +
                     "is_vip, vip_expire_date, warning_count, is_banned, " +
                     "register_time, last_login_time FROM users WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByPhone(String phone) {
        String sql = "SELECT user_id, phone, nickname, avatar_url, password_hash, " +
                     "is_vip, vip_expire_date, warning_count, is_banned, " +
                     "register_time, last_login_time FROM users WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增：根据手机号和密码查找用户（登录验证）
    @Override
    public User findByPhoneAndPassword(String phone, String passwordHash) {
        String sql = "SELECT user_id, phone, nickname, avatar_url, password_hash, " +
                     "is_vip, vip_expire_date, warning_count, is_banned, " +
                     "register_time, last_login_time FROM users WHERE phone = ? AND password_hash = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, md5(passwordHash));  // 对传入的密码进行MD5加密后比较
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByNickname(String nickname) {
        String sql = "SELECT user_id, phone, nickname, avatar_url, password_hash, " +
                     "is_vip, vip_expire_date, warning_count, is_banned, " +
                     "register_time, last_login_time FROM users WHERE nickname = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users (phone, nickname, avatar_url, password_hash, is_vip, register_time) " +
                     "VALUES (?, ?, ?, ?, 0, GETDATE())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getPhone());
            ps.setString(2, user.getNickname());
            ps.setString(3, user.getAvatarUrl());
            ps.setString(4, md5(user.getPasswordHash()));  // 密码加密存储
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET nickname = ?, avatar_url = ?, is_vip = ?, " +
                     "vip_expire_date = ?, warning_count = ?, is_banned = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNickname());
            ps.setString(2, user.getAvatarUrl());
            ps.setBoolean(3, user.getIsVip());
            ps.setTimestamp(4, user.getVipExpireDate() == null ? null : new Timestamp(user.getVipExpireDate().getTime()));
            ps.setInt(5, user.getWarningCount());
            ps.setBoolean(6, user.getIsBanned());
            ps.setInt(7, user.getUserId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 新增：修改密码
    @Override
    public int updatePassword(Integer userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, md5(newPasswordHash));
            ps.setInt(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 新增：检查手机号是否已存在
    @Override
    public boolean existsByPhone(String phone) {
        String sql = "SELECT 1 FROM users WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int updateLastLoginTime(Integer userId) {
        String sql = "UPDATE users SET last_login_time = GETDATE() WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateNickname(Integer userId, String nickname) {
        String sql = "UPDATE users SET nickname = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setInt(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateAvatar(Integer userId, String avatarUrl) {
        String sql = "UPDATE users SET avatar_url = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatarUrl);
            ps.setInt(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int addWarningCount(Integer userId) {
        String sql = "UPDATE users SET warning_count = warning_count + 1 WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int affected = ps.executeUpdate();
            // 检查是否需要自动封禁
            String checkSql = "UPDATE users SET is_banned = 1 WHERE user_id = ? AND warning_count >= 3 AND is_banned = 0";
            try (PreparedStatement ps2 = conn.prepareStatement(checkSql)) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
            return affected;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int banUser(Integer userId) {
        String sql = "UPDATE users SET is_banned = 1 WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int unbanUser(Integer userId) {
        String sql = "UPDATE users SET is_banned = 0, warning_count = 0 WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<User> findAll(int page, int size) {
        List<User> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT user_id, phone, nickname, avatar_url, is_vip, " +
                     "vip_expire_date, warning_count, is_banned, register_time, last_login_time " +
                     "FROM users ORDER BY user_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setPhone(rs.getString("phone"));
        user.setNickname(rs.getString("nickname"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setIsVip(rs.getBoolean("is_vip"));
        user.setVipExpireDate(rs.getTimestamp("vip_expire_date"));
        user.setWarningCount(rs.getInt("warning_count"));
        user.setIsBanned(rs.getBoolean("is_banned"));
        user.setRegisterTime(rs.getTimestamp("register_time"));
        user.setLastLoginTime(rs.getTimestamp("last_login_time"));
        return user;
    }
}