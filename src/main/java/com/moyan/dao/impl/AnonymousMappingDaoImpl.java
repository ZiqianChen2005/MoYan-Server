package com.moyan.dao.impl;

import com.moyan.dao.AnonymousMappingDao;
import com.moyan.entity.AnonymousMapping;
import com.moyan.util.DBUtil;
import java.sql.*;

public class AnonymousMappingDaoImpl implements AnonymousMappingDao {

    @Override
    public AnonymousMapping findByPostAndUser(Integer postId, Integer userId) {
        String sql = "SELECT mapping_id, post_id, user_id, anonymous_num " +
                     "FROM anonymous_mapping WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AnonymousMapping mapping = new AnonymousMapping();
                mapping.setMappingId(rs.getInt("mapping_id"));
                mapping.setPostId(rs.getInt("post_id"));
                mapping.setUserId(rs.getInt("user_id"));
                mapping.setAnonymousNum(rs.getInt("anonymous_num"));
                return mapping;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert(AnonymousMapping mapping) {
        String sql = "INSERT INTO anonymous_mapping (post_id, user_id, anonymous_num) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mapping.getPostId());
            ps.setInt(2, mapping.getUserId());
            ps.setInt(3, mapping.getAnonymousNum());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getNextAnonymousNum(Integer postId) {
        String sql = "SELECT ISNULL(MAX(anonymous_num), 0) + 1 FROM anonymous_mapping WHERE post_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}