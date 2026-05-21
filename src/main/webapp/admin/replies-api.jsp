<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%@ page import="com.moyan.entity.Reply"%>
<%@ page import="com.moyan.entity.Post"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.SQLException"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    ReplyDao replyDao = new ReplyDaoImpl();
    PostDao postDao = new PostDaoImpl();
    
    // 获取参数
    String statusStr = request.getParameter("status");
    String pageStr = request.getParameter("page");
    String sizeStr = request.getParameter("size");
    String keyword = request.getParameter("keyword");
    String postIdStr = request.getParameter("postId");
    
    int status = 0;
    int pageNum = 1;
    int size = 10;
    Integer filterPostId = null;
    
    if (statusStr != null && !statusStr.isEmpty()) status = Integer.parseInt(statusStr);
    if (pageStr != null && !pageStr.isEmpty()) pageNum = Integer.parseInt(pageStr);
    if (sizeStr != null && !sizeStr.isEmpty()) size = Integer.parseInt(sizeStr);
    if (postIdStr != null && !postIdStr.isEmpty()) filterPostId = Integer.parseInt(postIdStr);
    
    List<Reply> list = new ArrayList<>();
    int totalCount = 0;
    
    // 根据状态查询
    if (status == 0) {
        list = replyDao.findPendingList(pageNum, size);
        totalCount = replyDao.countPending();
    } else {
        // 已通过(status=1)或已拒绝(status=2) - 使用通用查询
        list = getRepliesByStatus(replyDao, status, pageNum, size, keyword, filterPostId);
        totalCount = countRepliesByStatus(status, keyword, filterPostId);
    }
    
    // 为每条回复补充帖子标题
    for (Reply reply : list) {
        Post post = postDao.findByPostId(reply.getPostId());
        if (post != null) {
            reply.setPostTitle(post.getTitle());
        }
    }
    
    int totalPages = (totalCount + size - 1) / size;
    int pendingCount = replyDao.countPending();
    
    // 构建JSON响应
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"list\":[");
    for (int i = 0; i < list.size(); i++) {
        Reply r = list.get(i);
        if (i > 0) json.append(",");
        json.append("{");
        json.append("\"replyId\":").append(r.getReplyId()).append(",");
        json.append("\"postId\":").append(r.getPostId()).append(",");
        json.append("\"userId\":").append(r.getUserId()).append(",");
        json.append("\"isAnonymous\":").append(r.getIsAnonymous()).append(",");
        json.append("\"content\":\"").append(escapeJson(r.getContent())).append("\",");
        json.append("\"replyTime\":\"").append(r.getReplyTime() != null ? r.getReplyTime().toString() : "").append("\",");
        json.append("\"status\":").append(r.getStatus()).append(",");
        json.append("\"postTitle\":\"").append(escapeJson(r.getPostTitle())).append("\",");
        json.append("\"authorNickname\":\"").append(escapeJson(r.getAuthorNickname() != null ? r.getAuthorNickname() : "")) .append("\"");
        json.append("}");
    }
    json.append("],");
    json.append("\"totalPages\":").append(totalPages).append(",");
    json.append("\"totalCount\":").append(totalCount).append(",");
    json.append("\"pendingCount\":").append(pendingCount);
    json.append("}");
    
    out.print(json.toString());
%>
<%!
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    private List<Reply> getRepliesByStatus(ReplyDao replyDao, int status, int page, int size, String keyword, Integer filterPostId) {
        List<Reply> list = new ArrayList<>();
        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder(
            "SELECT r.*, u.nickname as author_nickname " +
            "FROM replies r LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.status = ? "
        );
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND r.content LIKE ? ");
        }
        if (filterPostId != null) {
            sql.append("AND r.post_id = ? ");
        }
        sql.append("ORDER BY r.reply_time DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = com.moyan.util.DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, status);
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            if (filterPostId != null) {
                ps.setInt(paramIndex++, filterPostId);
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reply reply = new Reply();
                reply.setReplyId(rs.getInt("reply_id"));
                reply.setPostId(rs.getInt("post_id"));
                reply.setUserId(rs.getInt("user_id"));
                reply.setIsAnonymous(rs.getBoolean("is_anonymous"));
                reply.setContent(rs.getString("content"));
                reply.setReplyTime(rs.getTimestamp("reply_time"));
                reply.setStatus(rs.getInt("status"));
                try { reply.setAuthorNickname(rs.getString("author_nickname")); } catch (Exception e) {}
                list.add(reply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private int countRepliesByStatus(int status, String keyword, Integer filterPostId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM replies WHERE status = ? ");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND content LIKE ? ");
        }
        if (filterPostId != null) {
            sql.append("AND post_id = ? ");
        }
        try (Connection conn = com.moyan.util.DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, status);
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            if (filterPostId != null) {
                ps.setInt(paramIndex++, filterPostId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
%>