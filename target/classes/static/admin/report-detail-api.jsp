<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReportDaoImpl"%>
<%@ page import="com.moyan.dao.ReportDao"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%@ page import="com.moyan.entity.Report"%>
<%@ page import="com.moyan.entity.Post"%>
<%@ page import="com.moyan.entity.Reply"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    int reportId = Integer.parseInt(request.getParameter("reportId"));
    
    ReportDao reportDao = new ReportDaoImpl();
    Report report = reportDao.findByReportId(reportId);
    
    if (report == null) {
        out.print("{\"error\":\"举报不存在\"}");
        return;
    }
    
    // 获取被举报内容
    String targetContent = "";
    PostDao postDao = new PostDaoImpl();
    ReplyDao replyDao = new ReplyDaoImpl();
    
    if (report.getTargetType() == 1) {  // 帖子
        Post post = postDao.findByPostId(report.getTargetId());
        if (post != null) {
            targetContent = post.getTitle() + "\n\n" + post.getContent();
        }
    } else if (report.getTargetType() == 2) {  // 回复
        Reply reply = replyDao.findByReplyId(report.getTargetId());
        if (reply != null) {
            targetContent = reply.getContent();
        }
    }
    
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"reportId\":").append(report.getReportId()).append(",");
    json.append("\"reason\":\"").append(escapeJson(report.getReason())).append("\",");
    json.append("\"targetContent\":\"").append(escapeJson(targetContent)).append("\"");
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
%>