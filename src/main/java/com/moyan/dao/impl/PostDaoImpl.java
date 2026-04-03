package com.moyan.dao.impl;

import com.moyan.dao.PostDao;
import com.moyan.entity.Post;
import com.moyan.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDaoImpl implements PostDao {

    @Override
    public Post findByPostId(Integer postId) {
        String sql = "SELECT p.*, u.nickname as author_nickname " +
                     "FROM posts p LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractPost(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert(Post post) {
        String sql = "INSERT INTO posts (user_id, is_anonymous, title, content, tags, is_newbie, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getUserId());
            ps.setBoolean(2, post.getIsAnonymous());
            ps.setString(3, post.getTitle());
            ps.setString(4, post.getContent());
            ps.setString(5, post.getTags());
            ps.setBoolean(6, post.getIsNewbie());
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
    public int updateStatus(Integer postId, Integer status, String rejectReason) {
        String sql = "UPDATE posts SET status = ? WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, postId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateViewCount(Integer postId) {
        String sql = "UPDATE posts SET view_count = view_count + 1 WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateScore(Integer postId, Double totalScore) {
        String sql = "UPDATE posts SET total_score = ? WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, totalScore);
            ps.setInt(2, postId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Post> findPendingList(int page, int size) {
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT p.*, u.nickname as author_nickname " +
                     "FROM posts p LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.status = 0 ORDER BY p.post_time ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Post> findApprovedList(int page, int size, String tag, String keyword) {
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, u.nickname as author_nickname, " +
            "(SELECT AVG(article_score * 0.55 + tag_accuracy * 0.3) FROM ratings WHERE post_id = p.post_id) as total_score " +
            "FROM posts p LEFT JOIN users u ON p.user_id = u.user_id " +
            "WHERE p.status = 1 "
        );
        
        if (tag != null && !tag.isEmpty()) {
            sql.append("AND p.tags LIKE ? ");
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (p.title LIKE ? OR p.content LIKE ?) ");
        }
        sql.append("ORDER BY p.post_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (tag != null && !tag.isEmpty()) {
                ps.setString(paramIndex++, "%" + tag + "%");
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
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Post> findRecommendedList(int page, int size, int currentUserId) {
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;
        // 综合评分 = 文章评分55% + tag精确度30% + 打赏量15%
        String sql = "SELECT p.*, u.nickname as author_nickname, " +
                     "COALESCE((" +
                     "   SELECT AVG(r.article_score * 0.55 + r.tag_accuracy * 0.3) " +
                     "   FROM ratings r WHERE r.post_id = p.post_id" +
                     "), 0) + " +
                     "COALESCE((" +
                     "   SELECT SUM(t.amount * 0.15 / 100) " +
                     "   FROM tips t WHERE t.post_id = p.post_id" +
                     "), 0) as total_score " +
                     "FROM posts p LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.status = 1 " +
                     "ORDER BY total_score DESC, p.post_time DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Post> findByUserId(Integer userId, int page, int size) {
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT p.*, u.nickname as author_nickname " +
                     "FROM posts p LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.user_id = ? ORDER BY p.post_time DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM posts WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = 0";
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

    @Override
    public int countByStatus(Integer status) {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Post extractPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("post_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setIsAnonymous(rs.getBoolean("is_anonymous"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setTags(rs.getString("tags"));
        post.setPostTime(rs.getTimestamp("post_time"));
        post.setIsNewbie(rs.getBoolean("is_newbie"));
        post.setStatus(rs.getInt("status"));
        post.setViewCount(rs.getInt("view_count"));
        try {
            post.setAuthorNickname(rs.getString("author_nickname"));
        } catch (SQLException e) {}
        try {
            post.setTotalScore(rs.getDouble("total_score"));
        } catch (SQLException e) {}
        return post;
    }
}