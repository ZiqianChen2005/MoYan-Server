package com.moyan.controller;

import com.google.gson.Gson;
import com.moyan.entity.Response;
import com.moyan.service.*;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private Gson gson = new Gson();
//    private UserService userService = new UserService();
//    private PostService postService = new PostService();
//    private ReplyService replyService = new ReplyService();
//    private RatingService ratingService = new RatingService();
//    private TipService tipService = new TipService();
//    private ReportService reportService = new ReportService();
//    private TaskService taskService = new TaskService();
    
    /**
     * 处理客户端请求
     * @param requestJson 格式: {"action":"login", "params":{...}}
     * @return 响应JSON
     */
    public String handle(String requestJson) {
        try {
            Map<String, Object> request = gson.fromJson(requestJson, Map.class);
            String action = (String) request.get("action");
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            
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
                    
                // 帖子相关
                case "createPost":
                    return handleCreatePost(params);
                case "getPostList":
                    return handleGetPostList(params);
                case "getPostDetail":
                    return handleGetPostDetail(params);
                case "searchPosts":
                    return handleSearchPosts(params);
                    
                // 回复相关
                case "createReply":
                    return handleCreateReply(params);
                case "getReplies":
                    return handleGetReplies(params);
                    
                // 评分打赏
                case "ratePost":
                    return handleRatePost(params);
                case "tipPost":
                    return handleTipPost(params);
                    
                // 举报
                case "report":
                    return handleReport(params);
                    
                // 互动任务
                case "getDailyTask":
                    return handleGetDailyTask(params);
                case "submitTaskAnswer":
                    return handleSubmitTaskAnswer(params);
                case "getTopAnswers":
                    return handleGetTopAnswers(params);
                    
                default:
                    return gson.toJson(Response.fail("未知操作: " + action));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return gson.toJson(Response.fail("服务器错误: " + e.getMessage()));
        }
    }
    
    // ==================== 用户相关 ====================
    
    private String handleLogin(Map<String, Object> params) {
        String phone = (String) params.get("phone");
        // 测试阶段验证码固定123456
        String code = (String) params.get("code");
        if (!"123456".equals(code)) {
            return gson.toJson(Response.fail("验证码错误"));
        }
        return gson.toJson(userService.login(phone));
    }
    
    private String handleRegister(Map<String, Object> params) {
        String phone = (String) params.get("phone");
        String nickname = (String) params.get("nickname");
        return gson.toJson(userService.register(phone, nickname));
    }
    
    private String handleGetUserInfo(Map<String, Object> params) {
        int userId = ((Double) params.get("userId")).intValue();
        return gson.toJson(userService.getUserInfo(userId));
    }
    
    private String handleUpdateNickname(Map<String, Object> params) {
        int userId = ((Double) params.get("userId")).intValue();
        String nickname = (String) params.get("nickname");
        return gson.toJson(userService.updateNickname(userId, nickname));
    }
    
    private String handleUpdateAvatar(Map<String, Object> params) {
        int userId = ((Double) params.get("userId")).intValue();
        String avatarUrl = (String) params.get("avatarUrl");
        return gson.toJson(userService.updateAvatar(userId, avatarUrl));
    }
    
    // ==================== 帖子相关 ====================
    
    private String handleCreatePost(Map<String, Object> params) {
        int userId = ((Double) params.get("userId")).intValue();
        boolean isAnonymous = (boolean) params.getOrDefault("isAnonymous", false);
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        String tags = (String) params.get("tags");
        return gson.toJson(postService.createPost(userId, isAnonymous, title, content, tags));
    }
    
    private String handleGetPostList(Map<String, Object> params) {
        int page = ((Double) params.getOrDefault("page", 1)).intValue();
        int size = ((Double) params.getOrDefault("size", 20)).intValue();
        return gson.toJson(postService.getPostList(page, size));
    }
    
    private String handleGetPostDetail(Map<String, Object> params) {
        int postId = ((Double) params.get("postId")).intValue();
        int currentUserId = params.containsKey("userId") ? 
            ((Double) params.get("userId")).intValue() : -1;
        return gson.toJson(postService.getPostDetail(postId, currentUserId));
    }
    
    private String handleSearchPosts(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        String tag = (String) params.get("tag");
        String sortBy = (String) params.getOrDefault("sortBy", "time");
        int page = ((Double) params.getOrDefault("page", 1)).intValue();
        return gson.toJson(postService.searchPosts(keyword, tag, sortBy, page));
    }
    
    // ==================== 回复相关 ====================
    
    private String handleCreateReply(Map<String, Object> params) {
        int postId = ((Double) params.get("postId")).intValue();
        int userId = ((Double) params.get("userId")).intValue();
        boolean isAnonymous = (boolean) params.getOrDefault("isAnonymous", false);
        String content = (String) params.get("content");
        return gson.toJson(replyService.createReply(postId, userId, isAnonymous, content));
    }
    
    private String handleGetReplies(Map<String, Object> params) {
        int postId = ((Double) params.get("postId")).intValue();
        int page = ((Double) params.getOrDefault("page", 1)).intValue();
        return gson.toJson(replyService.getReplies(postId, page));
    }
    
    // ==================== 评分打赏 ====================
    
    private String handleRatePost(Map<String, Object> params) {
        int postId = ((Double) params.get("postId")).intValue();
        int userId = ((Double) params.get("userId")).intValue();
        int tagAccuracy = ((Double) params.get("tagAccuracy")).intValue();
        int articleScore = ((Double) params.get("articleScore")).intValue();
        String comment = (String) params.get("comment");
        return gson.toJson(ratingService.ratePost(postId, userId, tagAccuracy, articleScore, comment));
    }
    
    private String handleTipPost(Map<String, Object> params) {
        int postId = ((Double) params.get("postId")).intValue();
        int fromUserId = ((Double) params.get("fromUserId")).intValue();
        double amount = ((Double) params.get("amount")).doubleValue();
        return gson.toJson(tipService.tipPost(postId, fromUserId, amount));
    }
    
    // ==================== 举报 ====================
    
    private String handleReport(Map<String, Object> params) {
        int reporterId = ((Double) params.get("reporterId")).intValue();
        int targetType = ((Double) params.get("targetType")).intValue();
        int targetId = ((Double) params.get("targetId")).intValue();
        String reason = (String) params.get("reason");
        return gson.toJson(reportService.report(reporterId, targetType, targetId, reason));
    }
    
    // ==================== 互动任务 ====================
    
    private String handleGetDailyTask(Map<String, Object> params) {
        return gson.toJson(taskService.getTodayTask());
    }
    
    private String handleSubmitTaskAnswer(Map<String, Object> params) {
        int taskId = ((Double) params.get("taskId")).intValue();
        int userId = ((Double) params.get("userId")).intValue();
        String content = (String) params.get("content");
        return gson.toJson(taskService.submitAnswer(taskId, userId, content));
    }
    
    private String handleGetTopAnswers(Map<String, Object> params) {
        int taskId = ((Double) params.get("taskId")).intValue();
        int limit = ((Double) params.getOrDefault("limit", 3)).intValue();
        return gson.toJson(taskService.getTopAnswers(taskId, limit));
    }
}