<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    int replyId = Integer.parseInt(request.getParameter("replyId"));
    int status = Integer.parseInt(request.getParameter("status"));
    String note = request.getParameter("note");
    
    ReplyDao replyDao = new ReplyDaoImpl();
    int result = replyDao.updateStatus(replyId, status);
    
    String json = "{\"success\":" + (result > 0) + "}";
    out.print(json);
%>