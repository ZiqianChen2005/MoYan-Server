package com.moyan.dao.impl;

import com.moyan.dao.RatingDao;
import com.moyan.entity.Rating;
import com.moyan.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDaoImpl implements RatingDao {

    private static final Logger log = LoggerFactory.getLogger(RatingDaoImpl.class);

    @Override
    public Rating findByPostAndUser(Integer postId, Integer userId) {
        String sql = "SELECT rating_id, post_id, user_id, tag_accuracy, article_score, comment, rating_time " +
                "FROM ratings WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractRating(rs);
            }
        } catch (SQLException e) {
            log.error("根据帖子ID和用户ID查询评分失败, postId={}, userId={}", postId, userId, e);
        }
        return null;
    }

    @Override
    public int insert(Rating rating) {
        String sql = "INSERT INTO ratings (post_id, user_id, tag_accuracy, article_score, comment) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rating.getPostId());
            ps.setInt(2, rating.getUserId());
            ps.setInt(3, rating.getTagAccuracy());
            ps.setInt(4, rating.getArticleScore());
            ps.setString(5, rating.getComment());
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("插入评分失败, rating={}", rating, e);
        }
        return 0;
    }

    @Override
    public List<Rating> findByPostId(Integer postId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT rating_id, post_id, user_id, tag_accuracy, article_score, comment, rating_time " +
                "FROM ratings WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractRating(rs));
            }
        } catch (SQLException e) {
            log.error("根据帖子ID查询评分列表失败, postId={}", postId, e);
        }
        return list;
    }

    @Override
    public double getAvgTagAccuracy(Integer postId) {
        String sql = "SELECT AVG(CAST(tag_accuracy AS FLOAT)) FROM ratings WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            log.error("获取帖子平均标签准确度失败, postId={}", postId, e);
        }
        return 0;
    }

    @Override
    public double getAvgArticleScore(Integer postId) {
        String sql = "SELECT AVG(CAST(article_score AS FLOAT)) FROM ratings WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            log.error("获取帖子平均文章评分失败, postId={}", postId, e);
        }
        return 0;
    }

    @Override
    public int countByPostId(Integer postId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计帖子评分数量失败, postId={}", postId, e);
        }
        return 0;
    }

    private Rating extractRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setRatingId(rs.getInt("rating_id"));
        rating.setPostId(rs.getInt("post_id"));
        rating.setUserId(rs.getInt("user_id"));
        rating.setTagAccuracy(rs.getInt("tag_accuracy"));
        rating.setArticleScore(rs.getInt("article_score"));
        rating.setComment(rs.getString("comment"));
        rating.setRatingTime(rs.getTimestamp("rating_time"));
        return rating;
    }
}