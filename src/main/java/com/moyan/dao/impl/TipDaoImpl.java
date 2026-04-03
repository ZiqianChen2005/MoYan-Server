package com.moyan.dao.impl;

import com.moyan.dao.TipDao;
import com.moyan.entity.Tip;
import com.moyan.util.DBUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipDaoImpl implements TipDao {

    @Override
    public int insert(Tip tip) {
        String sql = "INSERT INTO tips (from_user_id, to_user_id, post_id, amount, platform_fee, author_income) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tip.getFromUserId());
            ps.setInt(2, tip.getToUserId());
            ps.setInt(3, tip.getPostId());
            ps.setBigDecimal(4, tip.getAmount());
            ps.setBigDecimal(5, tip.getPlatformFee());
            ps.setBigDecimal(6, tip.getAuthorIncome());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Tip> findByToUserId(Integer toUserId, int page, int size) {
        List<Tip> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT tip_id, from_user_id, to_user_id, post_id, amount, platform_fee, author_income, tip_time " +
                     "FROM tips WHERE to_user_id = ? ORDER BY tip_time DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toUserId);
            ps.setInt(2, offset);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractTip(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Tip> findByPostId(Integer postId) {
        List<Tip> list = new ArrayList<>();
        String sql = "SELECT tip_id, from_user_id, to_user_id, post_id, amount, platform_fee, author_income, tip_time " +
                     "FROM tips WHERE post_id = ? ORDER BY tip_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractTip(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public BigDecimal getTotalTipAmountByPostId(Integer postId) {
        String sql = "SELECT ISNULL(SUM(amount), 0) FROM tips WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalTipAmountByUserId(Integer userId) {
        String sql = "SELECT ISNULL(SUM(author_income), 0) FROM tips WHERE to_user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private Tip extractTip(ResultSet rs) throws SQLException {
        Tip tip = new Tip();
        tip.setTipId(rs.getInt("tip_id"));
        tip.setFromUserId(rs.getInt("from_user_id"));
        tip.setToUserId(rs.getInt("to_user_id"));
        tip.setPostId(rs.getInt("post_id"));
        tip.setAmount(rs.getBigDecimal("amount"));
        tip.setPlatformFee(rs.getBigDecimal("platform_fee"));
        tip.setAuthorIncome(rs.getBigDecimal("author_income"));
        tip.setTipTime(rs.getTimestamp("tip_time"));
        return tip;
    }
}