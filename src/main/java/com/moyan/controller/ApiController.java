package com.moyan.controller;

import com.google.gson.Gson;
import com.moyan.dto.Response;
import com.moyan.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 陌言服务端 API 控制器
 * 所有接口通过 RESTful API 调用，返回 JSON 格式数据
 *
 * @author MoYan Team
 * @version 2.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private TipService tipService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DailyTaskService taskService;

    private final Gson gson = new Gson();

    // ==================== 1. 认证授权模块 ====================

    @RestController
    @RequestMapping("/api/auth")
    @Tag(name = "01-认证授权", description = "用户登录、注册、密码管理相关接口")
    public class AuthController {

        @PostMapping("/login")
        @Operation(summary = "用户登录",
                description = "使用手机号和密码登录系统，密码为明文传输（后续升级HTTPS）")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "请求成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "登录成功",
                            "data": {
                                "userId": 1,
                                "phone": "13800138000",
                                "nickname": "张三",
                                "avatarUrl": "http://example.com/avatar.jpg",
                                "warningCount": 0,
                                "isBanned": false
                            }
                        }"""))),
                @ApiResponse(responseCode = "200", description = "登录失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "手机号或密码错误",
                            "data": null
                        }""")))
        })
        public String login(
                @Parameter(description = "手机号", required = true, example = "13800138000")
                @RequestParam String phone,
                @Parameter(description = "密码（明文，6-20位）", required = true, example = "123456")
                @RequestParam String password) {
            Response<?> resp = userService.login(phone, password);
            return gson.toJson(resp);
        }

        @PostMapping("/register")
        @Operation(summary = "用户注册",
                description = "使用手机号、密码和昵称注册新账号")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "注册成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "注册成功",
                            "data": {
                                "userId": 1,
                                "phone": "13800138000",
                                "nickname": "张三",
                                "avatarUrl": ""
                            }
                        }"""))),
                @ApiResponse(responseCode = "200", description = "注册失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "手机号已存在",
                            "data": null
                        }""")))
        })
        public String register(
                @Parameter(description = "手机号", required = true, example = "13800138000")
                @RequestParam String phone,
                @Parameter(description = "密码（至少6位）", required = true, example = "123456")
                @RequestParam String password,
                @Parameter(description = "昵称（2-20位）", required = true, example = "张三")
                @RequestParam String nickname) {
            Response<?> resp = userService.register(phone, password, nickname);
            return gson.toJson(resp);
        }

        @PostMapping("/password/update")
        @Operation(summary = "修改密码",
                description = "通过旧密码修改为新密码，新密码长度6-20位")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "修改成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "密码修改成功",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "200", description = "修改失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "原密码错误",
                            "data": null
                        }""")))
        })
        public String updatePassword(
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "原密码", required = true, example = "123456")
                @RequestParam String oldPassword,
                @Parameter(description = "新密码（6-20位）", required = true, example = "654321")
                @RequestParam String newPassword) {
            Response<?> resp = userService.updatePassword(userId, oldPassword, newPassword);
            return gson.toJson(resp);
        }
    }

    // ==================== 2. 用户中心模块 ====================

    @RestController
    @RequestMapping("/api/user")
    @Tag(name = "02-用户中心", description = "用户信息管理相关接口")
    public class UserController {

        @GetMapping("/info")
        @Operation(summary = "获取用户信息",
                description = "根据用户ID获取用户详细信息")
        public String getUserInfo(
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId) {
            Response<?> resp = userService.getUserInfo(userId);
            return gson.toJson(resp);
        }

        @PutMapping("/nickname")
        @Operation(summary = "修改昵称",
                description = "更新用户昵称，长度2-20位")
        public String updateNickname(
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "新昵称（2-20位）", required = true, example = "李四")
                @RequestParam String nickname) {
            Response<?> resp = userService.updateNickname(userId, nickname);
            return gson.toJson(resp);
        }

        @PutMapping("/avatar")
        @Operation(summary = "修改头像",
                description = "更新用户头像URL")
        public String updateAvatar(
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "头像URL", required = true, example = "http://example.com/avatar.jpg")
                @RequestParam String avatarUrl) {
            Response<?> resp = userService.updateAvatar(userId, avatarUrl);
            return gson.toJson(resp);
        }
    }

    // ==================== 3. 帖子管理模块 ====================

    @RestController
    @RequestMapping("/api/post")
    @Tag(name = "03-帖子管理", description = "帖子发布、查询、搜索、审核相关接口")
    public class PostController {

        @PostMapping("/create")
        @Operation(summary = "发布帖子",
                description = "用户发布新帖子，支持匿名发布，需管理员审核后可见")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "发布成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": 123
                        }""", description = "返回帖子ID"))),
                @ApiResponse(responseCode = "200", description = "发布失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户已被封禁",
                            "data": null
                        }""")))
        })
        public String createPost(
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "是否匿名", example = "false")
                @RequestParam(defaultValue = "false") Boolean isAnonymous,
                @Parameter(description = "标题（最多100字）", required = true, example = "我的第一篇诗歌")
                @RequestParam String title,
                @Parameter(description = "内容", required = true, example = "这是帖子内容...")
                @RequestParam String content,
                @Parameter(description = "标签（逗号分隔）", example = "诗歌,现代诗")
                @RequestParam(required = false) String tags) {
            Response<?> resp = postService.createPost(userId, isAnonymous, title, content, tags);
            return gson.toJson(resp);
        }

        @GetMapping("/list")
        @Operation(summary = "获取帖子列表（首页推荐流）",
                description = "分页获取帖子列表，支持推荐算法排序")
        @Parameters({
                @Parameter(name = "page", description = "页码", example = "1"),
                @Parameter(name = "size", description = "每页数量", example = "20"),
                @Parameter(name = "userId", description = "当前用户ID（可选，用于判断是否已点赞/评分）", example = "1")
        })
        public String getPostList(
                @RequestParam(defaultValue = "1") Integer page,
                @RequestParam(defaultValue = "20") Integer size,
                @RequestParam(required = false) Integer userId) {
            Response<?> resp = postService.getPostList(page, size, userId);
            return gson.toJson(resp);
        }

        @GetMapping("/detail")
        @Operation(summary = "获取帖子详情",
                description = "获取帖子的完整信息，包括所有回复和互动数据")
        @Parameters({
                @Parameter(name = "postId", description = "帖子ID", required = true, example = "1"),
                @Parameter(name = "userId", description = "当前用户ID（可选，用于判断是否已点赞/评分）", example = "1")
        })
        public String getPostDetail(
                @RequestParam Integer postId,
                @RequestParam(required = false) Integer userId) {
            Response<?> resp = postService.getPostDetail(postId, userId);
            return gson.toJson(resp);
        }

        @GetMapping("/search")
        @Operation(summary = "搜索帖子",
                description = "根据关键词或标签搜索帖子，支持多种排序方式")
        @Parameters({
                @Parameter(name = "keyword", description = "搜索关键词", example = "诗歌"),
                @Parameter(name = "tag", description = "标签筛选", example = "现代诗"),
                @Parameter(name = "sortBy", description = "排序方式：time(最新)/hot(最热)/score(最高分)",
                        example = "time", schema = @Schema(allowableValues = {"time", "hot", "score"})),
                @Parameter(name = "page", description = "页码", example = "1")
        })
        public String searchPosts(
                @RequestParam(required = false) String keyword,
                @RequestParam(required = false) String tag,
                @RequestParam(defaultValue = "time") String sortBy,
                @RequestParam(defaultValue = "1") Integer page) {
            Response<?> resp = postService.searchPosts(keyword, tag, sortBy, page);
            return gson.toJson(resp);
        }

        @GetMapping("/user")
        @Operation(summary = "获取用户发布的帖子",
                description = "获取指定用户发布的所有帖子，支持分页")
        @Parameters({
                @Parameter(name = "userId", description = "用户ID", required = true, example = "1"),
                @Parameter(name = "page", description = "页码", example = "1"),
                @Parameter(name = "size", description = "每页数量", example = "20")
        })
        public String getPostsByUserId(
                @RequestParam Integer userId,
                @RequestParam(defaultValue = "1") Integer page,
                @RequestParam(defaultValue = "20") Integer size) {
            Response<?> resp = postService.getPostsByUserId(userId, page, size);
            return gson.toJson(resp);
        }
    }

    // ==================== 4. 回复管理模块 ====================

    @RestController
    @RequestMapping("/api/reply")
    @Tag(name = "04-回复管理", description = "回复发布、查询、审核相关接口")
    public class ReplyController {

        @PostMapping("/create")
        @Operation(summary = "发布回复",
                description = "对帖子发布回复，支持匿名，需管理员审核后可见")
        public String createReply(
                @Parameter(description = "帖子ID", required = true, example = "1")
                @RequestParam Integer postId,
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "是否匿名", example = "false")
                @RequestParam(defaultValue = "false") Boolean isAnonymous,
                @Parameter(description = "回复内容", required = true, example = "写得真好！")
                @RequestParam String content) {
            Response<?> resp = replyService.createReply(postId, userId, isAnonymous, content);
            return gson.toJson(resp);
        }

        @GetMapping("/list")
        @Operation(summary = "获取回复列表",
                description = "分页获取指定帖子的所有回复")
        @Parameters({
                @Parameter(name = "postId", description = "帖子ID", required = true, example = "1"),
                @Parameter(name = "page", description = "页码", example = "1")
        })
        public String getReplies(
                @RequestParam Integer postId,
                @RequestParam(defaultValue = "1") Integer page) {
            Response<?> resp = replyService.getReplies(postId, page);
            return gson.toJson(resp);
        }
    }

    // ==================== 5. 互动功能模块 ====================

    @RestController
    @RequestMapping("/api/interact")
    @Tag(name = "05-互动功能", description = "评分、打赏、举报相关接口")
    public class InteractController {

        @PostMapping("/rate")
        @Operation(summary = "给帖子评分",
                description = "对帖子进行评分（标签准确度1-5分 + 文章质量1-5分）")
        public String ratePost(
                @Parameter(description = "帖子ID", required = true, example = "1")
                @RequestParam Integer postId,
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "标签准确度评分（1-5分）", required = true, example = "4")
                @RequestParam Integer tagAccuracy,
                @Parameter(description = "文章质量评分（1-5分）", required = true, example = "5")
                @RequestParam Integer articleScore,
                @Parameter(description = "评语", example = "写得很好，继续加油！")
                @RequestParam(required = false) String comment) {
            Response<?> resp = ratingService.ratePost(postId, userId, tagAccuracy, articleScore, comment);
            return gson.toJson(resp);
        }

        @PostMapping("/tip")
        @Operation(summary = "打赏帖子",
                description = "打赏帖子，平台抽成8%")
        public String tipPost(
                @Parameter(description = "帖子ID", required = true, example = "1")
                @RequestParam Integer postId,
                @Parameter(description = "打赏用户ID", required = true, example = "1")
                @RequestParam Integer fromUserId,
                @Parameter(description = "打赏金额（元）", required = true, example = "10")
                @RequestParam BigDecimal amount) {
            Response<?> resp = tipService.tipPost(postId, fromUserId, amount);
            return gson.toJson(resp);
        }

        @PostMapping("/report")
        @Operation(summary = "举报内容",
                description = "举报违规的帖子或回复")
        @Parameters({
                @Parameter(name = "reporterId", description = "举报人ID", required = true, example = "1"),
                @Parameter(name = "targetType", description = "目标类型：1-帖子，2-回复", required = true,
                        example = "1", schema = @Schema(allowableValues = {"1", "2"})),
                @Parameter(name = "targetId", description = "目标ID", required = true, example = "1"),
                @Parameter(name = "reason", description = "举报原因", required = true, example = "内容违规")
        })
        public String report(
                @RequestParam Integer reporterId,
                @RequestParam Integer targetType,
                @RequestParam Integer targetId,
                @RequestParam String reason) {
            Response<?> resp = reportService.report(reporterId, targetType, targetId, reason);
            return gson.toJson(resp);
        }
    }

    // ==================== 6. 每日任务模块 ====================

    @RestController
    @RequestMapping("/api/task")
    @Tag(name = "06-每日任务", description = "互动任务功能相关接口")
    public class DailyTaskController {

        @GetMapping("/today")
        @Operation(summary = "获取今日互动任务",
                description = "获取今日的续写任务或其他互动任务")
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                    {
                        "code": 0,
                        "msg": "success",
                        "data": {
                            "taskId": 1,
                            "taskType": 1,
                            "title": "续写任务",
                            "content": "原文内容..."
                        }
                    }""")))
        public String getTodayTask() {
            Response<?> resp = taskService.getTodayTask();
            return gson.toJson(resp);
        }

        @PostMapping("/submit")
        @Operation(summary = "提交任务回答",
                description = "提交今日任务的回答内容，每个用户每天只能提交一次")
        public String submitTaskAnswer(
                @Parameter(description = "任务ID", required = true, example = "1")
                @RequestParam Integer taskId,
                @Parameter(description = "用户ID", required = true, example = "1")
                @RequestParam Integer userId,
                @Parameter(description = "回答内容", required = true, example = "这是我对任务的回答...")
                @RequestParam String content) {
            Response<?> resp = taskService.submitAnswer(taskId, userId, content);
            return gson.toJson(resp);
        }

        @GetMapping("/top-answers")
        @Operation(summary = "获取任务优质回答",
                description = "获取指定任务的前N名高分优质回答")
        @Parameters({
                @Parameter(name = "taskId", description = "任务ID", required = true, example = "1"),
                @Parameter(name = "limit", description = "获取数量", example = "3")
        })
        public String getTopAnswers(
                @RequestParam Integer taskId,
                @RequestParam(defaultValue = "3") Integer limit) {
            Response<?> resp = taskService.getTopAnswers(taskId, limit);
            return gson.toJson(resp);
        }

        @GetMapping("/check-submitted")
        @Operation(summary = "检查是否已提交今日任务",
                description = "检查用户今日是否已经提交过任务")
        @Parameters({
                @Parameter(name = "taskId", description = "任务ID", required = true, example = "1"),
                @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
        })
        public String hasSubmitted(
                @RequestParam Integer taskId,
                @RequestParam Integer userId) {
            Response<?> resp = taskService.hasSubmitted(taskId, userId);
            return gson.toJson(resp);
        }
    }

    // ==================== 7. 管理员模块 ====================

    @RestController
    @RequestMapping("/api/admin")
    @Tag(name = "07-管理员功能", description = "帖子审核、回复审核、举报处理、用户管理相关接口")
    public class AdminController {

        // ----- 帖子审核 -----
        @PostMapping("/post/approve")
        @Operation(summary = "通过帖子审核",
                description = "管理员审核通过帖子，帖子正式发布")
        @Parameters({
                @Parameter(name = "postId", description = "帖子ID", required = true, example = "1"),
                @Parameter(name = "adminId", description = "管理员ID", required = true, example = "1")
        })
        public String approvePost(
                @RequestParam Integer postId,
                @RequestParam Integer adminId) {
            Response<?> resp = postService.approvePost(postId, adminId);
            return gson.toJson(resp);
        }

        @PostMapping("/post/reject")
        @Operation(summary = "拒绝帖子审核",
                description = "管理员拒绝帖子审核，需填写拒绝原因")
        @Parameters({
                @Parameter(name = "postId", description = "帖子ID", required = true, example = "1"),
                @Parameter(name = "adminId", description = "管理员ID", required = true, example = "1"),
                @Parameter(name = "reason", description = "拒绝原因", required = true, example = "内容违规")
        })
        public String rejectPost(
                @RequestParam Integer postId,
                @RequestParam Integer adminId,
                @RequestParam String reason) {
            Response<?> resp = postService.rejectPost(postId, adminId, reason);
            return gson.toJson(resp);
        }

        // ----- 回复审核 -----
        @PostMapping("/reply/approve")
        @Operation(summary = "通过回复审核",
                description = "管理员审核通过回复")
        @Parameters({
                @Parameter(name = "replyId", description = "回复ID", required = true, example = "1"),
                @Parameter(name = "adminId", description = "管理员ID", required = true, example = "1")
        })
        public String approveReply(
                @RequestParam Integer replyId,
                @RequestParam Integer adminId) {
            Response<?> resp = replyService.approveReply(replyId, adminId);
            return gson.toJson(resp);
        }

        @PostMapping("/reply/reject")
        @Operation(summary = "拒绝回复审核",
                description = "管理员拒绝回复审核")
        @Parameters({
                @Parameter(name = "replyId", description = "回复ID", required = true, example = "1"),
                @Parameter(name = "adminId", description = "管理员ID", required = true, example = "1"),
                @Parameter(name = "reason", description = "拒绝原因", required = true, example = "违规内容")
        })
        public String rejectReply(
                @RequestParam Integer replyId,
                @RequestParam Integer adminId,
                @RequestParam String reason) {
            Response<?> resp = replyService.rejectReply(replyId, adminId, reason);
            return gson.toJson(resp);
        }

        // ----- 举报处理 -----
        @PostMapping("/report/handle")
        @Operation(summary = "处理举报",
                description = "管理员处理举报内容")
        @Parameters({
                @Parameter(name = "reportId", description = "举报ID", required = true, example = "1"),
                @Parameter(name = "handlerId", description = "处理人ID", required = true, example = "1"),
                @Parameter(name = "action", description = "处理动作：1-撤下并警告，2-仅警告，3-驳回举报",
                        required = true, example = "1", schema = @Schema(allowableValues = {"1", "2", "3"})),
                @Parameter(name = "note", description = "处理备注", example = "已警告用户")
        })
        public String handleReport(
                @RequestParam Integer reportId,
                @RequestParam Integer handlerId,
                @RequestParam Integer action,
                @RequestParam(required = false) String note) {
            Response<?> resp = reportService.handleReport(reportId, handlerId, action, note);
            return gson.toJson(resp);
        }

        // ----- 用户管理 -----
        @PostMapping("/user/warning")
        @Operation(summary = "警告用户",
                description = "警告用户，警告3次自动封禁")
        @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
        public String addWarning(@RequestParam Integer userId) {
            Response<?> resp = userService.addWarning(userId);
            return gson.toJson(resp);
        }

        @PostMapping("/user/ban")
        @Operation(summary = "封禁用户",
                description = "封禁用户账号，封禁后无法发布帖子/回复")
        @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
        public String banUser(@RequestParam Integer userId) {
            Response<?> resp = userService.banUser(userId);
            return gson.toJson(resp);
        }

        @PostMapping("/user/unban")
        @Operation(summary = "解封用户",
                description = "解除用户封禁状态")
        @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
        public String unbanUser(@RequestParam Integer userId) {
            Response<?> resp = userService.unbanUser(userId);
            return gson.toJson(resp);
        }
    }

    // ==================== 8. 公共接口 ====================

    @RestController
    @RequestMapping("/api/public")
    @Tag(name = "08-公共接口", description = "健康检查、系统信息等公共接口")
    public class PublicController {

        @GetMapping("/health")
        @Operation(summary = "健康检查",
                description = "检查服务是否正常运行，用于监控和负载均衡")
        @ApiResponse(responseCode = "200", description = "服务正常",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                    {
                        "code": 0,
                        "msg": "服务运行正常",
                        "data": {
                            "status": "UP",
                            "timestamp": "2026-06-13 11:04:35",
                            "version": "2.1-SNAPSHOT"
                        }
                    }""")))
        public Response<Map<String, Object>> health() {
            return Response.success(Map.of(
                    "status", "UP",
                    "timestamp", new java.util.Date(),
                    "version", "2.1-SNAPSHOT"
            ));
        }

        @GetMapping("/info")
        @Operation(summary = "系统信息",
                description = "获取系统版本、API文档等信息")
        public Response<Map<String, String>> systemInfo() {
            return Response.success(Map.of(
                    "name", "陌言服务端",
                    "version", "2.1-SNAPSHOT",
                    "description", "文学社交平台服务端",
                    "apiDocs", "/api/public/docs"
            ));
        }
    }
}