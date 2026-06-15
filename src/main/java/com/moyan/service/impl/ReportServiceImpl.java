package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.dto.Response;
import com.moyan.entity.*;
import com.moyan.service.ReportService;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private ReportDao reportDao = new ReportDaoImpl();
    private PostDao postDao = new PostDaoImpl();
    private ReplyDao replyDao = new ReplyDaoImpl();
    private UserDao userDao = new UserDaoImpl();

    // 原有方法保持不变...

    @Override
    public Response<Void> report(Integer reporterId, Integer targetType, Integer targetId, String reason) {
        // 检查是否重复举报
        Report existing = reportDao.findByReporterAndTarget(reporterId, targetType, targetId);
        if (existing != null && existing.getStatus() == 0) {
            return Response.fail("您已经举报过了，请等待处理");
        }

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setStatus(0);
        report.setReportTime(new Date());

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
        if (report.getStatus() != 0) {
            return Response.fail("该举报已处理");
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

    @Override
    public Response<Map<String, Object>> getReportList(Integer status, Integer page, Integer size, Integer targetType) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        List<Report> reports = reportDao.findByStatus(status, page, size, targetType);
        int total = reportDao.countByStatus(status, targetType);
        int pendingCount = reportDao.countByStatus(0, null);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Report report : reports) {
            Map<String, Object> item = new HashMap<>();
            item.put("reportId", report.getReportId());
            item.put("reporterId", report.getReporterId());
            item.put("targetType", report.getTargetType());
            item.put("targetId", report.getTargetId());
            item.put("reason", report.getReason());
            item.put("reportTime", report.getReportTime());
            item.put("status", report.getStatus());
            item.put("handlerId", report.getHandlerId());
            item.put("handleTime", report.getHandleTime());
            item.put("handleNote", report.getHandleNote());

            // 获取举报人昵称
            User reporter = userDao.findByUserId(report.getReporterId());
            item.put("reporterNickname", reporter != null ? reporter.getNickname() : "未知用户");

            // 获取被举报内容
            if (report.getTargetType() == 1) {
                Post post = postDao.findByPostId(report.getTargetId());
                item.put("targetContent", post != null ? post.getContent() : "帖子已删除");
                item.put("targetTitle", post != null ? post.getTitle() : "");
            } else if (report.getTargetType() == 2) {
                Reply reply = replyDao.findByReplyId(report.getTargetId());
                item.put("targetContent", reply != null ? reply.getContent() : "回复已删除");
                item.put("targetTitle", "");
            }

            // 获取处理人昵称
            if (report.getHandlerId() != null) {
                User handler = userDao.findByUserId(report.getHandlerId());
                item.put("handlerNickname", handler != null ? handler.getNickname() : "管理员");
            }

            result.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", result);
        data.put("totalPages", (total + size - 1) / size);
        data.put("pendingCount", pendingCount);
        data.put("total", total);

        return Response.success(data);
    }

    @Override
    public Response<Map<String, Object>> getReportDetail(Integer reportId) {
        Report report = reportDao.findByReportId(reportId);
        if (report == null) {
            return Response.fail("举报不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("reportId", report.getReportId());
        data.put("reporterId", report.getReporterId());
        data.put("targetType", report.getTargetType());
        data.put("targetId", report.getTargetId());
        data.put("reason", report.getReason());
        data.put("reportTime", report.getReportTime());
        data.put("status", report.getStatus());

        // 获取被举报内容
        if (report.getTargetType() == 1) {
            Post post = postDao.findByPostId(report.getTargetId());
            data.put("targetContent", post != null ? post.getContent() : "帖子已删除");
            data.put("targetTitle", post != null ? post.getTitle() : "");
        } else if (report.getTargetType() == 2) {
            Reply reply = replyDao.findByReplyId(report.getTargetId());
            data.put("targetContent", reply != null ? reply.getContent() : "回复已删除");
            data.put("postTitle", reply != null ? reply.getPostTitle() : "");
        }

        return Response.success(data);
    }
}