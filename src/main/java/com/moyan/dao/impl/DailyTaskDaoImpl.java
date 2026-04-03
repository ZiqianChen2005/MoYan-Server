package com.moyan.dao.impl;

import com.moyan.dao.DailyTaskDao;
import com.moyan.entity.DailyTask;
import com.moyan.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyTaskDaoImpl implements DailyTaskDao {

    @Override
    public DailyTask findByDate(Date date) {
        String sql = "SELECT task_id, task_type, title, content, publish_date, is_active " +
                     "FROM daily_tasks WHERE CAST(publish_date AS DATE) = CAST(? AS DATE) AND is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractTask(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert(DailyTask task) {
        String sql = "INSERT INTO daily_tasks (task_type, title, content, publish_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, task.getTaskType());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getContent());
            ps.setDate(4, new java.sql.Date(task.getPublishDate().getTime()));
            ps.setBoolean(5, task.getIsActive());
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
    public int update(DailyTask task) {
        String sql = "UPDATE daily_tasks SET task_type = ?, title = ?, content = ?, is_active = ? " +
                     "WHERE task_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getTaskType());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getContent());
            ps.setBoolean(4, task.getIsActive());
            ps.setInt(5, task.getTaskId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<DailyTask> findAll(int page, int size) {
        List<DailyTask> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT task_id, task_type, title, content, publish_date, is_active " +
                     "FROM daily_tasks ORDER BY publish_date DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractTask(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public DailyTask findByTaskId(Integer taskId) {
        String sql = "SELECT task_id, task_type, title, content, publish_date, is_active " +
                     "FROM daily_tasks WHERE task_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractTask(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DailyTask extractTask(ResultSet rs) throws SQLException {
        DailyTask task = new DailyTask();
        task.setTaskId(rs.getInt("task_id"));
        task.setTaskType(rs.getInt("task_type"));
        task.setTitle(rs.getString("title"));
        task.setContent(rs.getString("content"));
        task.setPublishDate(rs.getDate("publish_date"));
        task.setIsActive(rs.getBoolean("is_active"));
        return task;
    }
}