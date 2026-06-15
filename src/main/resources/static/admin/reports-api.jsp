<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moyan.dao.impl.ReportDaoImpl"%>
<%@ page import="com.moyan.dao.ReportDao"%>
<%@ page import="com.moyan.dao.impl.PostDaoImpl"%>
<%@ page import="com.moyan.dao.PostDao"%>
<%@ page import="com.moyan.dao.impl.ReplyDaoImpl"%>
<%@ page import="com.moyan.dao.ReplyDao"%>
<%@ page import="com.moyan.dao.impl.UserDaoImpl"%>
<%@ page import="com.moyan.dao.UserDao"%>
<%@ page import="com.moyan.entity.Report"%>
<%@ page import="com.moyan.entity.Post"%>
<%@ page import="com.moyan.entity.Reply"%>
<%@ page import="com.moyan.entity.User"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.*"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    
    String statusStr = request.getParameter("status");
    String pageStr = request.getParameter("page");
    String sizeStr = request.getParameter("size");
    String targetTypeStr = request.getParameter("targetType");
    
    int status = 0;
    int pageNum = 1;
    int size = 10;
    Integer filterTargetType = null;
    
    if (statusStr != null && !statusStr.isEmpty()) status = Integer.parseInt(statusStr);
    if (pageStr != null && !pageStr.isEmpty()) pageNum = Integer.parseInt(pageStr);
    if (sizeStr != null && !sizeStr.isEmpty()) size = Integer.parseInt(sizeStr);
    if (targetTypeStr != null && !targetTypeStr.isEmpty()) filterTargetType = Integer.parseInt(targetTypeStr);
    
    ReportDao reportDao = new ReportDaoImpl();
    PostDao postDao = new PostDaoImpl();
    ReplyDao replyDao = new ReplyDaoImpl();
    UserDao userDao = new UserDaoImpl();
    
    // 获取举报列表
    List<Report> list = reportDao.findByStatus(status, pageNum, size);
    int totalCount = reportDao.countByStatus(status);
    int totalPages = (totalCount + size - 1) / size;
    int pendingCount = reportDao.countByStatus(0);
    
    // 构建JSON响应
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"list\":[");
    
    int index = 0;
    for (Report r : list) {
        // 过滤类型（如果选择了筛选）
        if (filterTargetType != null && r.getTargetType() != filterTargetType) {
            continue;
        }
        
        if (index > 0) json.append(",");
        
        // 获取举报人昵称
        String reporterNickname = "";
        try {
            com.moyan.entity.User user = userDao.findByUserId(r.getReporterId());
            if (user != null) {
                reporterNickname = user.getNickname();
            }
        } catch (Exception e) {
            reporterNickname = "用户" + r.getReporterId();
        }
        
        // 获取被举报内容
        String targetContent = "";
        if (r.getTargetType() == 1) {  // 帖子
            Post post = postDao.findByPostId(r.getTargetId());
            if (post != null) {
                targetContent = post.getTitle() + "\n\n" + post.getContent();
            }
        } else if (r.getTargetType() == 2) {  // 回复
            Reply reply = replyDao.findByReplyId(r.getTargetId());
            if (reply != null) {
                targetContent = reply.getContent();
            }
        }
        
        json.append("{");
        json.append("\"reportId\":").append(r.getReportId()).append(",");
        json.append("\"reporterId\":").append(r.getReporterId()).append(",");
        json.append("\"targetType\":").append(r.getTargetType()).append(",");
        json.append("\"targetId\":").append(r.getTargetId()).append(",");
        json.append("\"reason\":\"").append(escapeJson(r.getReason())).append("\",");
        json.append("\"reportTime\":\"").append(r.getReportTime() != null ? r.getReportTime().toString() : "").append("\",");
        json.append("\"status\":").append(r.getStatus()).append(",");
        json.append("\"handlerId\":").append(r.getHandlerId() != null ? r.getHandlerId() : 0).append(",");
        json.append("\"handleNote\":\"").append(escapeJson(r.getHandleNote())).append("\",");
        json.append("\"handleTime\":\"").append(r.getHandleTime() != null ? r.getHandleTime().toString() : "").append("\",");
        json.append("\"reporterNickname\":\"").append(escapeJson(reporterNickname)).append("\",");
        json.append("\"targetContent\":\"").append(escapeJson(targetContent)).append("\"");
        json.append("}");
        
        index++;
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
%>