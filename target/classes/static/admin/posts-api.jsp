<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%@ page import="com.moyan.entity.Post"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.SQLException"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    PostDao postDao = new PostDaoImpl();
    
    int status = Integer.parseInt(request.getParameter("status"));
    int pageNum = Integer.parseInt(request.getParameter("page"));
    int size = Integer.parseInt(request.getParameter("size"));
    
    List<Post> list = new ArrayList<>();
    int totalCount = 0;
    
    if (status == 0) {
        // 待审核
        list = postDao.findPendingList(pageNum, size);
        totalCount = postDao.countPending();
    } else if (status == 1) {
        // 已通过（支持筛选）
        String tag = request.getParameter("tag");
        String keyword = request.getParameter("keyword");
        if (tag != null && tag.trim().isEmpty()) tag = null;
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        list = postDao.findApprovedList(pageNum, size, tag, keyword);
        totalCount = postDao.countByStatus(1);
    } else if (status == 2) {
        // 已拒绝
        // 注：PostDao中没有直接按status=2分页查询的方法，使用findByStatus方法（需添加）
        // 临时使用findApprovedList并修改条件（实际应在PostDao中添加findByStatus方法）
        // 这里直接模拟或使用SQL查询
        list = getPostsByStatus(postDao, 2, pageNum, size);
        totalCount = postDao.countByStatus(2);
    } else if (status == 3) {
        // 举报待处理（status=3）
        list = getPostsByStatus(postDao, 3, pageNum, size);
        totalCount = postDao.countByStatus(3);
    }
    
    int totalPages = (totalCount + size - 1) / size;
    int pendingCount = postDao.countPending();
    
    // 构建JSON响应
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"list\":[");
    for (int i = 0; i < list.size(); i++) {
        Post p = list.get(i);
        if (i > 0) json.append(",");
        json.append("{");
        json.append("\"postId\":").append(p.getPostId()).append(",");
        json.append("\"userId\":").append(p.getUserId()).append(",");
        json.append("\"isAnonymous\":").append(p.getIsAnonymous()).append(",");
        json.append("\"title\":\"").append(escapeJson(p.getTitle())).append("\",");
        json.append("\"content\":\"").append(escapeJson(p.getContent())).append("\",");
        json.append("\"tags\":\"").append(escapeJson(p.getTags() != null ? p.getTags() : "")).append("\",");
        json.append("\"postTime\":\"").append(p.getPostTime() != null ? p.getPostTime().toString() : "").append("\",");
        json.append("\"isNewbie\":").append(p.getIsNewbie()).append(",");
        json.append("\"status\":").append(p.getStatus()).append(",");
        json.append("\"viewCount\":").append(p.getViewCount()).append(",");
        json.append("\"authorNickname\":\"").append(escapeJson(p.getAuthorNickname() != null ? p.getAuthorNickname() : "")).append("\"");
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
    
    private List<Post> getPostsByStatus(PostDao postDao, int status, int page, int size) {
        // 通过SQL直接查询（简化实现，使用DBUtil）
        List<Post> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT p.*, u.nickname as author_nickname " +
                     "FROM posts p LEFT JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.status = ? ORDER BY p.post_time DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = com.moyan.util.DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, offset);
            ps.setInt(3, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
                try { post.setAuthorNickname(rs.getString("author_nickname")); } catch (Exception e) {}
                list.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
%>