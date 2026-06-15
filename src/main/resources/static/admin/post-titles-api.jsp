<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    List<Map<String, Object>> posts = new ArrayList<>();
    String sql = "SELECT post_id, title FROM posts WHERE status = 1 ORDER BY post_time DESC";
    
    try (Connection conn = com.moyan.util.DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Map<String, Object> post = new HashMap<>();
            post.put("postId", rs.getInt("post_id"));
            post.put("title", rs.getString("title"));
            posts.add(post);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    StringBuilder json = new StringBuilder();
    json.append("[");
    for (int i = 0; i < posts.size(); i++) {
        if (i > 0) json.append(",");
        json.append("{");
        json.append("\"postId\":").append(posts.get(i).get("postId")).append(",");
        json.append("\"title\":\"").append(escapeJson(posts.get(i).get("title").toString())).append("\"");
        json.append("}");
    }
    json.append("]");
    
    out.print(json.toString());
%>
<%!
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
%>