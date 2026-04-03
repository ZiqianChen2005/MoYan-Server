package com.moyan.dao.impl;

import com.moyan.dao.TaskAnswerDao;
import com.moyan.entity.TaskAnswer;
import com.moyan.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskAnswerDaoImpl implements TaskAnswerDao {

    @Override
    public int insert(TaskAnswer answer) {
        String sql = "INSERT INTO task_answers (task_id, user_id, content, score) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, answer.getTaskId());
            ps.setInt(2, answer.getUserId());
            ps.setString(3, answer.getContent());
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
    public int updateScore(Integer answerId, Integer score) {
        String sql = "UPDATE task_answers SET score = ? WHERE answer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setInt(2, answerId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<TaskAnswer> findByTaskId(Integer taskId, int limit) {
        List<TaskAnswer> list = new ArrayList<>();
        String sql = "SELECT a.*, u.nickname as user_nickname " +
                     "FROM task_answers a LEFT JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.task_id = ? ORDER BY a.score DESC, a.submit_time ASC";
        if (limit > 0) {
            sql += " OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            if (limit > 0) {
                ps.setInt(2, limit);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractAnswer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<TaskAnswer> findByUserId(Integer userId, int page, int size) {
        List<TaskAnswer> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT a.*, u.nickname as user_nickname " +
                     "FROM task_answers a LEFT JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ? ORDER BY a.submit_time DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractAnswer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean hasSubmitted(Integer taskId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM task_answers WHERE task_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TaskAnswer extractAnswer(ResultSet rs) throws SQLException {
        TaskAnswer answer = new TaskAnswer();
        answer.setAnswerId(rs.getInt("answer_id"));
        answer.setTaskId(rs.getInt("task_id"));
        answer.setUserId(rs.getInt("user_id"));
        answer.setContent(rs.getString("content"));
        answer.setScore(rs.getInt("score"));
        answer.setSubmitTime(rs.getTimestamp("submit_time"));
        try {
            answer.setUserNickname(rs.getString("user_nickname"));
        } catch (SQLException e) {}
        return answer;
    }
}