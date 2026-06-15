<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%@ page import="com.moyan.entity.Reply"%>
<%@ page import="com.moyan.entity.Post"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    int replyId = Integer.parseInt(request.getParameter("replyId"));
    
    ReplyDao replyDao = new ReplyDaoImpl();
    PostDao postDao = new PostDaoImpl();
    
    Reply reply = replyDao.findByReplyId(replyId);
    
    if (reply == null) {
        out.print("{\"error\":\"回复不存在\"}");
        return;
    }
    
    Post post = postDao.findByPostId(reply.getPostId());
    
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"replyId\":").append(reply.getReplyId()).append(",");
    json.append("\"postId\":").append(reply.getPostId()).append(",");
    json.append("\"userId\":").append(reply.getUserId()).append(",");
    json.append("\"isAnonymous\":").append(reply.getIsAnonymous()).append(",");
    json.append("\"content\":\"").append(escapeJson(reply.getContent())).append("\",");
    json.append("\"replyTime\":\"").append(reply.getReplyTime() != null ? reply.getReplyTime().toString() : "").append("\",");
    json.append("\"status\":").append(reply.getStatus()).append(",");
    json.append("\"postTitle\":\"").append(escapeJson(post != null ? post.getTitle() : "")).append("\",");
    json.append("\"authorNickname\":\"").append(escapeJson(reply.getAuthorNickname() != null ? reply.getAuthorNickname() : "")) .append("\"");
    json.append("}");
    
    out.print(json.toString());
%>
<%!
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
%>