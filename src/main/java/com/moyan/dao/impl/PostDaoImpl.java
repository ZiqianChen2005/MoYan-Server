package com.moyan.dao.impl;

import com.moyan.dao.PostDao;
import com.moyan.entity.Post;
import com.moyan.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDaoImpl implements PostDao {

    private static final Logger log = LoggerFactory.getLogger(PostDaoImpl.class);

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
            log.error("根据帖子ID查询帖子失败, postId={}", postId, e);
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
            log.error("插入帖子失败, post={}", post, e);
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
            log.error("更新帖子状态失败, postId={}, status={}", postId, status, e);
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
            log.error("更新帖子浏览量失败, postId={}", postId, e);
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
            log.error("更新帖子评分失败, postId={}, totalScore={}", postId, totalScore, e);
        }
        return 0;
    }

    @Override
    public List<Post> findPendingList(int page, int size) {
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;

        log.debug("查询待审核帖子列表, page={}, size={}, offset={}", page, size, offset);

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
            log.debug("查询到 {} 条待审核帖子", list.size());
        } catch (SQLException e) {
            log.error("查询待审核帖子列表失败, page={}, size={}", page, size, e);
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
            log.error("查询已通过审核帖子列表失败, page={}, size={}, tag={}, keyword={}", page, size, tag, keyword, e);
        }
        return list;
    }

    @Override
    public List<Post> findRecommendedList(int page, int size, int currentUserId) {
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;
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
            log.error("查询推荐帖子列表失败, page={}, size={}, currentUserId={}", page, size, currentUserId, e);
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
            log.error("根据用户ID查询帖子列表失败, userId={}, page={}, size={}", userId, page, size, e);
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
            log.error("统计用户帖子数量失败, userId={}", userId, e);
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
            log.error("统计待审核帖子数量失败", e);
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
            log.error("根据状态统计帖子数量失败, status={}", status, e);
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
        } catch (SQLException e) {
            // 字段不存在，忽略
        }
        try {
            post.setTotalScore(rs.getDouble("total_score"));
        } catch (SQLException e) {
            // 字段不存在，忽略
        }
        return post;
    }
}