<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    String replyIdsStr = request.getParameter("replyIds");
    int status = Integer.parseInt(request.getParameter("status"));
    
    String[] replyIds = replyIdsStr.split(",");
    ReplyDao replyDao = new ReplyDaoImpl();
    int successCount = 0;
    
    for (String idStr : replyIds) {
        int replyId = Integer.parseInt(idStr.trim());
        int result = replyDao.updateStatus(replyId, status);
        if (result > 0) successCount++;
    }
    
    String json = "{\"success\":" + (successCount > 0) + ", \"count\":" + successCount + "}";
    out.print(json);
%>