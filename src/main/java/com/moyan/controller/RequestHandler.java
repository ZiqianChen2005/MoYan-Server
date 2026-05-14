package com.moyan.controller;

import com.google.gson.Gson;
import com.moyan.dto.Response;
import com.moyan.service.*;
import com.moyan.service.impl.*;
import java.util.Map;

public class RequestHandler {
    
    private Gson gson = new Gson();
    
    private UserService userService = new UserServiceImpl();
    private PostService postService = new PostServiceImpl();
    private ReplyService replyService = new ReplyServiceImpl();
    private RatingService ratingService = new RatingServiceImpl();
    private TipService tipService = new TipServiceImpl();
    private ReportService reportService = new ReportServiceImpl();
    private DailyTaskService taskService = new DailyTaskServiceImpl();
    
    @SuppressWarnings("unchecked")
    public String handle(String requestJson) {
        try {
            Map<String, Object> request = gson.fromJson(requestJson, Map.class);
            String action = (String) request.get("action");
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            
            System.out.println("处理请求: " + action);
            
            switch (action) {
                // 用户相关
                case "login":
                    return handleLogin(params);
                case "register":
                    return handleRegister(params);
                case "getUserInfo":
                    return handleGetUserInfo(params);
                case "updateNickname":
                    return handleUpdateNickname(params);
                case "updateAvatar":
                    return handleUpdateAvatar(params);
                case "updatePassword":  // 新增：修改密码
                    return handleUpdatePassword(params);
                    
                // 帖子相关
                case "createPost":
                    return handleCreatePost(params);
                case "getPostList":
                    return handleGetPostList(params);
                case "getPostDetail":
                    return handleGetPostDetail(params);
                case "searchPosts":
                    return handleSearchPosts(params);
                case "approvePost":
                    return handleApprovePost(params);
                case "rejectPost":
                    return handleRejectPost(params);
                    
                // 回复相关
                case "createReply":
                    return handleCreateReply(params);
                case "getReplies":
                    return handleGetReplies(params);
                case "approveReply":
                    return handleApproveReply(params);
                case "rejectReply":
                    return handleRejectReply(params);
                    
                // 评分打赏
                case "ratePost":
                    return handleRatePost(params);
                case "tipPost":
                    return handleTipPost(params);
                    
                // 举报
                case "report":
                    return handleReport(params);
                case "handleReport":
                    return handleHandleReport(params);
                    
                // 互动任务
                case "getTodayTask":
                    return handleGetTodayTask(params);
                case "submitTaskAnswer":
                    return handleSubmitTaskAnswer(params);
                case "getTopAnswers":
                    return handleGetTopAnswers(params);
                case "hasSubmitted":
                    return handleHasSubmitted(params);
                    
                // 用户管理
                case "addWarning":
                    return handleAddWarning(params);
                case "banUser":
                    return handleBanUser(params);
                case "unbanUser":
                    return handleUnbanUser(params);
                    
                default:
                    return gson.toJson(Response.fail("未知操作: " + action));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return gson.toJson(Response.fail("服务器错误: " + e.getMessage()));
        }
    }
    
    // ==================== 用户相关 ====================
    
    // 修改：使用password代替code
    private String handleLogin(Map<String, Object> params) {
        String phone = (String) params.get("phone");
        String password = (String) params.get("password");  // 改为password
        Response<?> resp = userService.login(phone, password);
        return gson.toJson(resp);
    }
    
    // 修改：添加password参数
    private String handleRegister(Map<String, Object> params) {
        String phone = (String) params.get("phone");
        String password = (String) params.get("password");  // 新增
        String nickname = (String) params.get("nickname");
        Response<?> resp = userService.register(phone, password, nickname);
        return gson.toJson(resp);
    }
    
    // 新增：修改密码
    private String handleUpdatePassword(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");
        Response<?> resp = userService.updatePassword(userId, oldPassword, newPassword);
        return gson.toJson(resp);
    }
    
    private String handleGetUserInfo(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        Response<?> resp = userService.getUserInfo(userId);
        return gson.toJson(resp);
    }
    
    private String handleUpdateNickname(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        String nickname = (String) params.get("nickname");
        Response<?> resp = userService.updateNickname(userId, nickname);
        return gson.toJson(resp);
    }
    
    private String handleUpdateAvatar(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        String avatarUrl = (String) params.get("avatarUrl");
        Response<?> resp = userService.updateAvatar(userId, avatarUrl);
        return gson.toJson(resp);
    }
    
    // ==================== 帖子相关 ====================
    
    private String handleCreatePost(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        Boolean isAnonymous = (Boolean) params.getOrDefault("isAnonymous", false);
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String tags = (String) params.get("tags");
        Response<?> resp = postService.createPost(userId, isAnonymous, title, content, tags);
        return gson.toJson(resp);
    }
    
    private String handleGetPostList(Map<String, Object> params) {
        Integer page = params.get("page") != null ? ((Double) params.get("page")).intValue() : 1;
        Integer size = params.get("size") != null ? ((Double) params.get("size")).intValue() : 20;
        Integer currentUserId = params.get("userId") != null ? ((Double) params.get("userId")).intValue() : null;
        Response<?> resp = postService.getPostList(page, size, currentUserId);
        return gson.toJson(resp);
    }
    
    private String handleGetPostDetail(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer currentUserId = params.get("userId") != null ? ((Double) params.get("userId")).intValue() : null;
        Response<?> resp = postService.getPostDetail(postId, currentUserId);
        return gson.toJson(resp);
    }
    
    private String handleSearchPosts(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        String tag = (String) params.get("tag");
        String sortBy = (String) params.getOrDefault("sortBy", "time");
        Integer page = params.get("page") != null ? ((Double) params.get("page")).intValue() : 1;
        Response<?> resp = postService.searchPosts(keyword, tag, sortBy, page);
        return gson.toJson(resp);
    }
    
    private String handleApprovePost(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer adminId = ((Double) params.get("adminId")).intValue();
        Response<?> resp = postService.approvePost(postId, adminId);
        return gson.toJson(resp);
    }
    
    private String handleRejectPost(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer adminId = ((Double) params.get("adminId")).intValue();
        String reason = (String) params.get("reason");
        Response<?> resp = postService.rejectPost(postId, adminId, reason);
        return gson.toJson(resp);
    }
    
    // ==================== 回复相关 ====================
    
    private String handleCreateReply(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer userId = ((Double) params.get("userId")).intValue();
        Boolean isAnonymous = (Boolean) params.getOrDefault("isAnonymous", false);
        String content = (String) params.get("content");
        Response<?> resp = replyService.createReply(postId, userId, isAnonymous, content);
        return gson.toJson(resp);
    }
    
    private String handleGetReplies(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer page = params.get("page") != null ? ((Double) params.get("page")).intValue() : 1;
        Response<?> resp = replyService.getReplies(postId, page);
        return gson.toJson(resp);
    }
    
    private String handleApproveReply(Map<String, Object> params) {
        Integer replyId = ((Double) params.get("replyId")).intValue();
        Integer adminId = ((Double) params.get("adminId")).intValue();
        Response<?> resp = replyService.approveReply(replyId, adminId);
        return gson.toJson(resp);
    }
    
    private String handleRejectReply(Map<String, Object> params) {
        Integer replyId = ((Double) params.get("replyId")).intValue();
        Integer adminId = ((Double) params.get("adminId")).intValue();
        String reason = (String) params.get("reason");
        Response<?> resp = replyService.rejectReply(replyId, adminId, reason);
        return gson.toJson(resp);
    }
    
    // ==================== 评分打赏 ====================
    
    private String handleRatePost(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer userId = ((Double) params.get("userId")).intValue();
        Integer tagAccuracy = ((Double) params.get("tagAccuracy")).intValue();
        Integer articleScore = ((Double) params.get("articleScore")).intValue();
        String comment = (String) params.get("comment");
        Response<?> resp = ratingService.ratePost(postId, userId, tagAccuracy, articleScore, comment);
        return gson.toJson(resp);
    }
    
    private String handleTipPost(Map<String, Object> params) {
        Integer postId = ((Double) params.get("postId")).intValue();
        Integer fromUserId = ((Double) params.get("fromUserId")).intValue();
        java.math.BigDecimal amount = new java.math.BigDecimal(params.get("amount").toString());
        Response<?> resp = tipService.tipPost(postId, fromUserId, amount);
        return gson.toJson(resp);
    }
    
    // ==================== 举报 ====================
    
    private String handleReport(Map<String, Object> params) {
        Integer reporterId = ((Double) params.get("reporterId")).intValue();
        Integer targetType = ((Double) params.get("targetType")).intValue();
        Integer targetId = ((Double) params.get("targetId")).intValue();
        String reason = (String) params.get("reason");
        Response<?> resp = reportService.report(reporterId, targetType, targetId, reason);
        return gson.toJson(resp);
    }
    
    private String handleHandleReport(Map<String, Object> params) {
        Integer reportId = ((Double) params.get("reportId")).intValue();
        Integer handlerId = ((Double) params.get("handlerId")).intValue();
        Integer action = ((Double) params.get("action")).intValue();
        String note = (String) params.get("note");
        Response<?> resp = reportService.handleReport(reportId, handlerId, action, note);
        return gson.toJson(resp);
    }
    
    // ==================== 互动任务 ====================
    
    private String handleGetTodayTask(Map<String, Object> params) {
        Response<?> resp = taskService.getTodayTask();
        return gson.toJson(resp);
    }
    
    private String handleSubmitTaskAnswer(Map<String, Object> params) {
        Integer taskId = ((Double) params.get("taskId")).intValue();
        Integer userId = ((Double) params.get("userId")).intValue();
        String content = (String) params.get("content");
        Response<?> resp = taskService.submitAnswer(taskId, userId, content);
        return gson.toJson(resp);
    }
    
    private String handleGetTopAnswers(Map<String, Object> params) {
        Integer taskId = ((Double) params.get("taskId")).intValue();
        Integer limit = params.get("limit") != null ? ((Double) params.get("limit")).intValue() : 3;
        Response<?> resp = taskService.getTopAnswers(taskId, limit);
        return gson.toJson(resp);
    }
    
    private String handleHasSubmitted(Map<String, Object> params) {
        Integer taskId = ((Double) params.get("taskId")).intValue();
        Integer userId = ((Double) params.get("userId")).intValue();
        Response<?> resp = taskService.hasSubmitted(taskId, userId);
        return gson.toJson(resp);
    }
    
    // ==================== 用户管理 ====================
    
    private String handleAddWarning(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        Response<?> resp = userService.addWarning(userId);
        return gson.toJson(resp);
    }
    
    private String handleBanUser(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        Response<?> resp = userService.banUser(userId);
        return gson.toJson(resp);
    }
    
    private String handleUnbanUser(Map<String, Object> params) {
        Integer userId = ((Double) params.get("userId")).intValue();
        Response<?> resp = userService.unbanUser(userId);
        return gson.toJson(resp);
    }
}