package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.dto.Response;
import com.moyan.entity.*;
import com.moyan.service.ReportService;

public class ReportServiceImpl implements ReportService {
    
    private ReportDao reportDao = new ReportDaoImpl();
    private PostDao postDao = new PostDaoImpl();
    private ReplyDao replyDao = new ReplyDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    
    @Override
    public Response<Void> report(Integer reporterId, Integer targetType, Integer targetId, String reason) {
        // 检查是否重复举报（简化处理，实际应检查是否有待处理的举报）
        
        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        
        int result = reportDao.insert(report);
        if (result > 0) {
            // 如果是举报帖子，将帖子状态改为"已举报待处理"
            if (targetType == 1) {
                postDao.updateStatus(targetId, 3, null);
            }
            return Response.success(null);
        }
        return Response.fail("举报失败");
    }
    
    @Override
    public Response<Void> handleReport(Integer reportId, Integer handlerId, Integer action, String note) {
        // action: 1撤下内容并警告 2仅警告 3驳回举报
        Report report = reportDao.findByReportId(reportId);
        if (report == null) {
            return Response.fail("举报记录不存在");
        }
        
        int newStatus = 0;
        switch (action) {
            case 1:
            case 2:
                newStatus = 1;  // 已处理
                // 警告用户
                if (report.getTargetType() == 1) {
                    Post post = postDao.findByPostId(report.getTargetId());
                    if (post != null) {
                        userDao.addWarningCount(post.getUserId());
                        if (action == 1) {
                            // 撤下内容（设为已拒绝）
                            postDao.updateStatus(report.getTargetId(), 2, note);
                        }
                    }
                } else if (report.getTargetType() == 2) {
                    Reply reply = replyDao.findByReplyId(report.getTargetId());
                    if (reply != null) {
                        userDao.addWarningCount(reply.getUserId());
                        if (action == 1) {
                            replyDao.updateStatus(report.getTargetId(), 2);
                        }
                    }
                }
                break;
            case 3:
                newStatus = 2;  // 驳回
                // 恢复帖子状态
                if (report.getTargetType() == 1) {
                    postDao.updateStatus(report.getTargetId(), 1, null);
                }
                break;
            default:
                return Response.fail("无效的操作");
        }
        
        int result = reportDao.updateStatus(reportId, newStatus, handlerId, note);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("处理失败");
    }
}