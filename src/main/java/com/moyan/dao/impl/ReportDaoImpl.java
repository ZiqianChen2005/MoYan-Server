package com.moyan.dao.impl;

import com.moyan.dao.ReportDao;
import com.moyan.entity.Report;
import com.moyan.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoImpl implements ReportDao {

    private static final Logger log = LoggerFactory.getLogger(ReportDaoImpl.class);

    @Override
    public int insert(Report report) {
        String sql = "INSERT INTO reports (reporter_id, target_type, target_id, reason, status) " +
                "VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, report.getReporterId());
            ps.setInt(2, report.getTargetType());
            ps.setInt(3, report.getTargetId());
            ps.setString(4, report.getReason());
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("插入举报记录失败, report={}", report, e);
        }
        return 0;
    }

    @Override
    public int updateStatus(Integer reportId, Integer status, Integer handlerId, String handleNote) {
        String sql = "UPDATE reports SET status = ?, handler_id = ?, handle_time = GETDATE(), handle_note = ? " +
                "WHERE report_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            if (handlerId != null) {
                ps.setInt(2, handlerId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, handleNote);
            ps.setInt(4, reportId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("更新举报状态失败, reportId={}, status={}", reportId, status, e);
        }
        return 0;
    }

    @Override
    public List<Report> findByStatus(Integer status, int page, int size) {
        List<Report> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT report_id, reporter_id, target_type, target_id, reason, report_time, " +
                "status, handler_id, handle_time, handle_note " +
                "FROM reports WHERE status = ? ORDER BY report_time ASC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, offset);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractReport(rs));
            }
        } catch (SQLException e) {
            log.error("根据状态查询举报列表失败, status={}, page={}, size={}", status, page, size, e);
        }
        return list;
    }

    @Override
    public Report findByReportId(Integer reportId) {
        String sql = "SELECT report_id, reporter_id, target_type, target_id, reason, report_time, " +
                "status, handler_id, handle_time, handle_note FROM reports WHERE report_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractReport(rs);
            }
        } catch (SQLException e) {
            log.error("根据举报ID查询举报失败, reportId={}", reportId, e);
        }
        return null;
    }

    @Override
    public int countByStatus(Integer status) {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("根据状态统计举报数量失败, status={}", status, e);
        }
        return 0;
    }

    private Report extractReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("report_id"));
        report.setReporterId(rs.getInt("reporter_id"));
        report.setTargetType(rs.getInt("target_type"));
        report.setTargetId(rs.getInt("target_id"));
        report.setReason(rs.getString("reason"));
        report.setReportTime(rs.getTimestamp("report_time"));
        report.setStatus(rs.getInt("status"));
        report.setHandlerId(rs.getInt("handler_id"));
        if (rs.wasNull()) report.setHandlerId(null);
        report.setHandleTime(rs.getTimestamp("handle_time"));
        report.setHandleNote(rs.getString("handle_note"));
        return report;
    }
}