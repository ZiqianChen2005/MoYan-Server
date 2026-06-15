<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    int postId = Integer.parseInt(request.getParameter("postId"));
    int status = Integer.parseInt(request.getParameter("status")); // 1通过 2拒绝
    String note = request.getParameter("note");
    
    PostDao postDao = new PostDaoImpl();
    int result = postDao.updateStatus(postId, status, note);
    
    String json = "{\"success\":" + (result > 0) + "}";
    out.print(json);
%>