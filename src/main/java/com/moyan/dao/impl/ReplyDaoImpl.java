package com.moyan.dao.impl;

import com.moyan.dao.ReplyDao;
import com.moyan.entity.Reply;
import com.moyan.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplyDaoImpl implements ReplyDao {

    private static final Logger log = LoggerFactory.getLogger(ReplyDaoImpl.class);

    @Override
    public Reply findByReplyId(Integer replyId) {
        String sql = "SELECT r.*, u.nickname as author_nickname, p.title as post_title " +
                "FROM replies r LEFT JOIN users u ON r.user_id = u.user_id " +
                "LEFT JOIN posts p ON r.post_id = p.post_id " +
                "WHERE r.reply_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, replyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractReply(rs);
            }
        } catch (SQLException e) {
            log.error("根据回复ID查询回复失败, replyId={}", replyId, e);
        }
        return null;
    }

    @Override
    public int insert(Reply reply) {
        String sql = "INSERT INTO replies (post_id, user_id, is_anonymous, anonymous_num, content, status) " +
                "VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reply.getPostId());
            ps.setInt(2, reply.getUserId());
            ps.setBoolean(3, reply.getIsAnonymous());
            if (reply.getAnonymousNum() != null) {
                ps.setInt(4, reply.getAnonymousNum());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, reply.getContent());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("插入回复失败, reply={}", reply, e);
        }
        return -1;
    }

    @Override
    public int updateStatus(Integer replyId, Integer status) {
        String sql = "UPDATE replies SET status = ? WHERE reply_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, replyId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("更新回复状态失败, replyId={}, status={}", replyId, status, e);
        }
        return 0;
    }

    @Override
    public List<Reply> findByPostId(Integer postId, int page, int size) {
        List<Reply> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT r.*, u.nickname as author_nickname " +
                "FROM replies r LEFT JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.post_id = ? AND r.status = 1 " +
                "ORDER BY r.reply_time ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, offset);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReply(rs));
            }
        } catch (SQLException e) {
            log.error("根据帖子ID查询回复列表失败, postId={}, page={}, size={}", postId, page, size, e);
        }
        return list;
    }

    @Override
    public List<Reply> findPendingList(int page, int size) {
        List<Reply> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT r.*, u.nickname as author_nickname, p.title as post_title " +
                "FROM replies r LEFT JOIN users u ON r.user_id = u.user_id " +
                "LEFT JOIN posts p ON r.post_id = p.post_id " +
                "WHERE r.status = 0 ORDER BY r.reply_time ASC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReply(rs));
            }
        } catch (SQLException e) {
            log.error("查询待审核回复列表失败, page={}, size={}", page, size, e);
        }
        return list;
    }

    @Override
    public int countByPostId(Integer postId) {
        String sql = "SELECT COUNT(*) FROM replies WHERE post_id = ? AND status = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计帖子回复数量失败, postId={}", postId, e);
        }
        return 0;
    }

    @Override
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM replies WHERE status = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计待审核回复数量失败", e);
        }
        return 0;
    }

    @Override
    public int getAnonymousCount(Integer postId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM replies WHERE post_id = ? AND user_id = ? AND is_anonymous = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计用户匿名回复数量失败, postId={}, userId={}", postId, userId, e);
        }
        return 0;
    }

    private Reply extractReply(ResultSet rs) throws SQLException {
        Reply reply = new Reply();
        reply.setReplyId(rs.getInt("reply_id"));
        reply.setPostId(rs.getInt("post_id"));
        reply.setUserId(rs.getInt("user_id"));
        reply.setIsAnonymous(rs.getBoolean("is_anonymous"));
        reply.setAnonymousNum(rs.getInt("anonymous_num"));
        if (rs.wasNull()) reply.setAnonymousNum(null);
        reply.setContent(rs.getString("content"));
        reply.setReplyTime(rs.getTimestamp("reply_time"));
        reply.setStatus(rs.getInt("status"));
        try {
            reply.setAuthorNickname(rs.getString("author_nickname"));
        } catch (SQLException e) {}
        try {
            reply.setPostTitle(rs.getString("post_title"));
        } catch (SQLException e) {}
        return reply;
    }

    // 在 ReplyDaoImpl.java 中添加
    @Override
    public List<Reply> findByStatus(Integer status, int page, int size, String keyword, Integer postId) {
        List<Reply> list = new ArrayList<>();
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.nickname as author_nickname, p.title as post_title " +
                        "FROM replies r LEFT JOIN users u ON r.user_id = u.user_id " +
                        "LEFT JOIN posts p ON r.post_id = p.post_id " +
                        "WHERE r.status = ? "
        );

        if (postId != null && postId > 0) {
            sql.append("AND r.post_id = ? ");
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (r.content LIKE ? OR u.nickname LIKE ?) ");
        }
        sql.append("ORDER BY r.reply_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, status);
            if (postId != null && postId > 0) {
                ps.setInt(paramIndex++, postId);
            }
            if (keyword != null && !keyword.isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReply(rs));
            }
        } catch (SQLException e) {
            log.error("根据状态查询回复列表失败", e);
        }
        return list;
    }

    @Override
    public int countByStatus(Integer status, String keyword, Integer postId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM replies r WHERE r.status = ? ");
        if (postId != null && postId > 0) {
            sql.append("AND r.post_id = ? ");
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (r.content LIKE ? ) ");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, status);
            if (postId != null && postId > 0) {
                ps.setInt(paramIndex++, postId);
            }
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计回复数量失败", e);
        }
        return 0;
    }
}