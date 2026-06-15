<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReportDaoImpl"%>
<%@ page import="com.moyan.dao.ReportDao"%>
<%@ page import="com.moyan.entity.Report"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    int reportId = Integer.parseInt(request.getParameter("reportId"));
    int status = Integer.parseInt(request.getParameter("status"));  // 1:已处理 2:已驳回
    int handlerId = Integer.parseInt(request.getParameter("handlerId"));
    String handleNote = request.getParameter("handleNote");
    String action = request.getParameter("action");  // remove, warn, reject
    
    ReportDao reportDao = new ReportDaoImpl();
    Report report = reportDao.findByReportId(reportId);
    
    if (report == null) {
        out.print("{\"success\":false,\"message\":\"举报不存在\"}");
        return;
    }
    
    // 更新举报状态
    int result = reportDao.updateStatus(reportId, status, handlerId, handleNote);
    
    // 如果是撤下内容操作，更新帖子或回复状态
    if ("remove".equals(action) && result > 0) {
        if (report.getTargetType() == 1) {  // 帖子
            PostDao postDao = new PostDaoImpl();
            postDao.updateStatus(report.getTargetId(), 2, handleNote);  // 状态2=已拒绝
        } else if (report.getTargetType() == 2) {  // 回复
            ReplyDao replyDao = new ReplyDaoImpl();
            replyDao.updateStatus(report.getTargetId(), 2);  // 状态2=已拒绝
        }
    }
    
    String message = "";
    if ("remove".equals(action)) message = "已撤下内容并警告用户";
    else if ("warn".equals(action)) message = "已警告用户";
    else if ("reject".equals(action)) message = "已驳回举报";
    
    out.print("{\"success\":" + (result > 0) + ",\"message\":\"" + message + "\"}");
%>