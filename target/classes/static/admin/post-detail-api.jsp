<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%@ page import="com.moyan.entity.Post"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    int postId = Integer.parseInt(request.getParameter("postId"));
    PostDao postDao = new PostDaoImpl();
    Post post = postDao.findByPostId(postId);
    
    if (post == null) {
        out.print("{\"error\":\"帖子不存在\"}");
        return;
    }
    
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"postId\":").append(post.getPostId()).append(",");
    json.append("\"title\":\"").append(escapeJson(post.getTitle())).append("\",");
    json.append("\"content\":\"").append(escapeJson(post.getContent())).append("\"");
    json.append("}");
    
    out.print(json.toString());
%>
<%!
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
%>