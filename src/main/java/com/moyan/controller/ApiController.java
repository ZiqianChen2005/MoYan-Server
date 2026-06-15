package com.moyan.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

// ... existing code ...

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

    // ... existing code ...

    @RestController
    @RequestMapping("/api/auth")
    @Tag(name = "01-认证授权", description = "用户登录、注册、密码管理相关接口")
    public class AuthController {

        @PostMapping("/login")
        @Operation(summary = "用户登录",
                description = "使用手机号和密码登录系统，密码为明文传输（后续升级HTTPS）\n\n" +
                        "【README格式】{\"action\":\"login\",\"params\":{\"phone\":\"13800138000\",\"password\":\"123456\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "请求成功",
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
                @ApiResponse(responseCode = "1", description = "登录失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "手机号或密码错误",
                            "data": null
                        }""")))
        })
        public String login(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            String phone = (String) params.get("phone");
            String password = (String) params.get("password");
            Response<?> resp = userService.login(phone, password);
            return gson.toJson(resp);
        }

        @PostMapping("/register")
        @Operation(summary = "用户注册",
                description = "使用手机号、密码和昵称注册新账号\n\n" +
                        "【README格式】{\"action\":\"register\",\"params\":{\"phone\":\"13800138000\",\"password\":\"123456\",\"nickname\":\"张三\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "注册成功",
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
                @ApiResponse(responseCode = "1", description = "注册失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "手机号已存在",
                            "data": null
                        }""")))
        })
        public String register(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            String phone = (String) params.get("phone");
            String password = (String) params.get("password");
            String nickname = (String) params.get("nickname");
            Response<?> resp = userService.register(phone, password, nickname);
            return gson.toJson(resp);
        }

        @PostMapping("/password/update")
        @Operation(summary = "修改密码",
                description = "通过旧密码修改为新密码，新密码长度6-20位\n\n" +
                        "【README格式】{\"action\":\"updatePassword\",\"params\":{\"userId\":1,\"oldPassword\":\"123456\",\"newPassword\":\"654321\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "修改成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "密码修改成功",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "修改失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "原密码错误",
                            "data": null
                        }""")))
        })
        public String updatePassword(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            String oldPassword = (String) params.get("oldPassword");
            String newPassword = (String) params.get("newPassword");
            Response<?> resp = userService.updatePassword(userId, oldPassword, newPassword);
            return gson.toJson(resp);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录",
            description = "清除用户token，退出登录状态\n\n" +
                    "【README格式】{\"action\":\"logout\",\"params\":{\"userId\":1}}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "0", description = "退出成功",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "退出登录成功",
                            "data": null
                        }"""))),
            @ApiResponse(responseCode = "1", description = "退出失败",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户ID不能为空",
                            "data": null
                        }""")))
    })
    public String logout(@RequestBody Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        Integer userId = ((Number) params.get("userId")).intValue();
        Response<?> resp = userService.logout(userId);
        return gson.toJson(resp);
    }

    @PostMapping("/verify-token")
    @Operation(summary = "验证Token",
            description = "验证用户token是否有效，返回用户信息\n\n" +
                    "【README格式】{\"action\":\"verifyToken\",\"params\":{\"token\":\"eyJhbGci...\"}}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "0", description = "验证成功",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "token验证成功",
                            "data": {
                                "userId": 1,
                                "phone": "13800138000",
                                "nickname": "张三",
                                "avatarUrl": "http://example.com/avatar.jpg",
                                "warningCount": 0,
                                "isBanned": false,
                                "tokenExpireTime": "2026-06-21T10:00:00"
                            }
                        }"""))),
            @ApiResponse(responseCode = "1", description = "验证失败",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "token无效或已过期",
                            "data": null
                        }""")))
    })
    public String verifyToken(@RequestBody Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        String token = (String) params.get("token");
        Response<?> resp = userService.verifyToken(token);
        return gson.toJson(resp);
    }

    @PostMapping("/account/delete")
    @Operation(summary = "注销账号",
            description = "永久删除用户账号，需要验证密码，操作不可恢复\n\n" +
                    "【README格式】{\"action\":\"deleteAccount\",\"params\":{\"userId\":1,\"password\":\"123456\"}}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "0", description = "注销成功",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "账号注销成功",
                            "data": null
                        }"""))),
            @ApiResponse(responseCode = "1", description = "注销失败",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "密码错误",
                            "data": null
                        }""")))
    })
    public String deleteAccount(@RequestBody Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        Integer userId = ((Number) params.get("userId")).intValue();
        String password = (String) params.get("password");
        Response<?> resp = userService.deleteAccount(userId, password);
        return gson.toJson(resp);
    }

    @RestController
    @RequestMapping("/api/user")
    @Tag(name = "02-用户中心", description = "用户信息管理相关接口")
    public class UserController {

        @PostMapping("/info")
        @Operation(summary = "获取用户信息",
                description = "根据用户ID获取用户详细信息\n\n" +
                        "【README格式】{\"action\":\"getUserInfo\",\"params\":{\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": {
                                "userId": 1,
                                "phone": "13800138000",
                                "nickname": "张三",
                                "avatarUrl": "http://example.com/avatar.jpg",
                                "warningCount": 0,
                                "isBanned": false,
                                "createTime": "2025-01-01T10:00:00"
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户不存在",
                            "data": null
                        }""")))
        })
        public String getUserInfo(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            Response<?> resp = userService.getUserInfo(userId);
            return gson.toJson(resp);
        }

        @PostMapping("/nickname/update")
        @Operation(summary = "修改昵称",
                description = "更新用户昵称，长度2-20位\n\n" +
                        "【README格式】{\"action\":\"updateNickname\",\"params\":{\"userId\":1,\"nickname\":\"新昵称\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "修改成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "昵称修改成功",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "修改失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "昵称长度不符合要求",
                            "data": null
                        }""")))
        })
        public String updateNickname(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            String nickname = (String) params.get("nickname");
            Response<?> resp = userService.updateNickname(userId, nickname);
            return gson.toJson(resp);
        }

        @PostMapping("/avatar/update")
        @Operation(summary = "修改头像",
                description = "更新用户头像URL\n\n" +
                        "【README格式】{\"action\":\"updateAvatar\",\"params\":{\"userId\":1,\"avatarUrl\":\"http://图片地址\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "修改成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "头像修改成功",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "修改失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "头像URL格式错误",
                            "data": null
                        }""")))
        })
        public String updateAvatar(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            String avatarUrl = (String) params.get("avatarUrl");
            Response<?> resp = userService.updateAvatar(userId, avatarUrl);
            return gson.toJson(resp);
        }
    }

    @RestController
    @RequestMapping("/api/post")
    @Tag(name = "03-帖子管理", description = "帖子发布、查询、搜索、审核相关接口")
    public class PostController {
        @PostMapping("/admin/pending")
        @Operation(summary = "获取待审核/已审核帖子列表（管理员）",
                description = "管理员获取帖子列表，支持按状态筛选")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功"),
                @ApiResponse(responseCode = "1", description = "获取失败")
        })
        public String getPostsByStatus(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 0;
            Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
            Integer size = params.get("size") != null ? ((Number) params.get("size")).intValue() : 10;
            String tag = (String) params.get("tag");
            String keyword = (String) params.get("keyword");

            Response<?> resp = postService.getPostsByStatus(status, page, size, tag, keyword);
            return gson.toJson(resp);
        }

        @PostMapping("/admin/pending/count")
        @Operation(summary = "获取待审核帖子数量")
        public String getPendingCount(@RequestBody Map<String, Object> request) {
            Response<?> resp = postService.getPendingCount();
            return gson.toJson(resp);
        }

        @PostMapping("/create")
        @Operation(summary = "发布帖子",
                description = "用户发布新帖子，支持匿名发布，需管理员审核后可见\n\n" +
                        "【README格式】{\"action\":\"createPost\",\"params\":{\"userId\":1,\"isAnonymous\":false,\"title\":\"标题\",\"content\":\"内容\",\"tags\":\"诗歌,现代诗\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "发布成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": 123
                        }""", description = "返回帖子ID"))),
                @ApiResponse(responseCode = "0", description = "发布失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户已被封禁",
                            "data": null
                        }""")))
        })
        public String createPost(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            Boolean isAnonymous = params.get("isAnonymous") != null ? (Boolean) params.get("isAnonymous") : false;
            String title = (String) params.get("title");
            String content = (String) params.get("content");
            String tags = (String) params.get("tags");
            Response<?> resp = postService.createPost(userId, isAnonymous, title, content, tags);
            return gson.toJson(resp);
        }

        @PostMapping("/list")
        @Operation(summary = "获取帖子列表（首页推荐流）",
                description = "分页获取帖子列表，支持推荐算法排序\n\n" +
                        "【README格式】{\"action\":\"getPostList\",\"params\":{\"page\":1,\"size\":20,\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": [
                                {
                                    "postId": 1,
                                    "title": "春天的诗",
                                    "authorName": "张三",
                                    "tags": ["诗歌", "现代诗"],
                                    "viewCount": 100,
                                    "replyCount": 10,
                                    "avgScore": 4.5,
                                    "createTime": "2025-05-26T10:00:00"
                                }
                            ]
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "暂无帖子",
                            "data": []
                        }""")))
        })
        public String getPostList(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
            Integer size = params.get("size") != null ? ((Number) params.get("size")).intValue() : 20;
            Integer userId = params.get("userId") != null ? ((Number) params.get("userId")).intValue() : null;
            Response<?> resp = postService.getPostList(page, size, userId);
            return gson.toJson(resp);
        }

        @PostMapping("/detail")
        @Operation(summary = "获取帖子详情",
                description = "获取帖子的完整信息，包括所有回复和互动数据\n\n" +
                        "【README格式】{\"action\":\"getPostDetail\",\"params\":{\"postId\":1,\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": {
                                "postId": 1,
                                "title": "春天的诗",
                                "content": "春天来了...",
                                "authorName": "张三",
                                "tags": ["诗歌", "现代诗"],
                                "viewCount": 100,
                                "replyCount": 10,
                                "avgScore": 4.5,
                                "createTime": "2025-05-26T10:00:00",
                                "replies": []
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "帖子不存在",
                            "data": null
                        }""")))
        })
        public String getPostDetail(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer userId = params.get("userId") != null ? ((Number) params.get("userId")).intValue() : null;
            Response<?> resp = postService.getPostDetail(postId, userId);
            return gson.toJson(resp);
        }

        @PostMapping("/search")
        @Operation(summary = "搜索帖子",
                description = "根据关键词或标签搜索帖子，支持多种排序方式\n\n" +
                        "【README格式】{\"action\":\"searchPosts\",\"params\":{\"keyword\":\"关键词\",\"tag\":\"标签\",\"sortBy\":\"time\",\"page\":1}}\n\n" +
                        "sortBy可选值：time（最新）、hot（最热）、score（最高分）")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "搜索成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": {
                                "total": 10,
                                "page": 1,
                                "size": 20,
                                "list": [
                                    {
                                        "postId": 1,
                                        "title": "春天的诗",
                                        "authorName": "张三",
                                        "tags": ["诗歌"],
                                        "viewCount": 100,
                                        "replyCount": 10,
                                        "avgScore": 4.5,
                                        "createTime": "2025-05-26T10:00:00"
                                    }
                                ]
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "搜索失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "未找到相关帖子",
                            "data": null
                        }""")))
        })
        public String searchPosts(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            String keyword = (String) params.get("keyword");
            String tag = (String) params.get("tag");
            String sortBy = params.get("sortBy") != null ? (String) params.get("sortBy") : "time";
            Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
            Response<?> resp = postService.searchPosts(keyword, tag, sortBy, page);
            return gson.toJson(resp);
        }

        @PostMapping("/user/posts")
        @Operation(summary = "获取用户发布的帖子",
                description = "获取指定用户发布的所有帖子，支持分页\n\n" +
                        "【README格式】{\"action\":\"getPostsByUserId\",\"params\":{\"userId\":1,\"page\":1,\"size\":20}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": {
                                "total": 5,
                                "page": 1,
                                "size": 20,
                                "list": [
                                    {
                                        "postId": 1,
                                        "title": "我的第一首诗",
                                        "tags": ["诗歌"],
                                        "viewCount": 50,
                                        "replyCount": 3,
                                        "avgScore": 4.0,
                                        "createTime": "2025-05-26T10:00:00"
                                    }
                                ]
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户不存在",
                            "data": null
                        }""")))
        })
        public String getPostsByUserId(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
            Integer size = params.get("size") != null ? ((Number) params.get("size")).intValue() : 20;
            Response<?> resp = postService.getPostsByUserId(userId, page, size);
            return gson.toJson(resp);
        }
    }

    @RestController
    @RequestMapping("/api/reply")
    @Tag(name = "04-回复管理", description = "回复发布、查询、审核相关接口")
    public class ReplyController {

        // 在 ApiController.java 的 ReplyController 类中添加

        @PostMapping("/admin/list")
        @Operation(summary = "获取回复列表（管理员）")
        public String getRepliesByStatus(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 0;
            Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
            Integer size = params.get("size") != null ? ((Number) params.get("size")).intValue() : 10;
            String keyword = (String) params.get("keyword");
            Integer postId = params.get("postId") != null ? ((Number) params.get("postId")).intValue() : null;

            Response<?> resp = replyService.getRepliesByStatus(status, page, size, keyword, postId);
            return gson.toJson(resp);
        }

        @PostMapping("/admin/batch")
        @Operation(summary = "批量审核回复")
        public String batchAuditReplies(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            String replyIds = (String) params.get("replyIds");
            Integer status = ((Number) params.get("status")).intValue();
            Integer adminId = ((Number) params.get("adminId")).intValue();
            String note = (String) params.get("note");

            Response<?> resp = replyService.batchAudit(replyIds, status, adminId, note);
            return gson.toJson(resp);
        }

        @PostMapping("/admin/detail")
        @Operation(summary = "获取回复详情（管理员）")
        public String getReplyDetail(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer replyId = ((Number) params.get("replyId")).intValue();

            Response<?> resp = replyService.getReplyDetail(replyId);
            return gson.toJson(resp);
        }

        @PostMapping("/post-titles")
        @Operation(summary = "获取帖子标题列表（用于筛选）")
        public String getPostTitles(@RequestBody Map<String, Object> request) {
            Response<?> resp = replyService.getPostTitles();
            return gson.toJson(resp);
        }

        @PostMapping("/create")
        @Operation(summary = "发布回复",
                description = "对帖子发布回复，支持匿名，需管理员审核后可见\n\n" +
                        "【README格式】{\"action\":\"createReply\",\"params\":{\"postId\":1,\"userId\":1,\"isAnonymous\":false,\"content\":\"回复内容\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "发布成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": 456
                        }""", description = "返回回复ID"))),
                @ApiResponse(responseCode = "1", description = "发布失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户已被封禁",
                            "data": null
                        }""")))
        })
        public String createReply(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer userId = ((Number) params.get("userId")).intValue();
            Boolean isAnonymous = params.get("isAnonymous") != null ? (Boolean) params.get("isAnonymous") : false;
            String content = (String) params.get("content");
            Response<?> resp = replyService.createReply(postId, userId, isAnonymous, content);
            return gson.toJson(resp);
        }

        @PostMapping("/list")
        @Operation(summary = "获取回复列表",
                description = "分页获取指定帖子的所有回复\n\n" +
                        "【README格式】{\"action\":\"getReplies\",\"params\":{\"postId\":1,\"page\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": [
                                {
                                    "replyId": 1,
                                    "authorName": "张三",
                                    "isAnonymous": false,
                                    "anonymousNum": null,
                                    "content": "写得真好，支持！",
                                    "replyTime": "2025-05-26T10:30:00"
                                },
                                {
                                    "replyId": 2,
                                    "authorName": "匿名用户",
                                    "isAnonymous": true,
                                    "anonymousNum": 1,
                                    "content": "匿名回复内容",
                                    "replyTime": "2025-05-26T11:00:00"
                                }
                            ]
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "帖子不存在",
                            "data": null
                        }""")))
        })
        public String getReplies(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
            Response<?> resp = replyService.getReplies(postId, page);
            return gson.toJson(resp);
        }
    }

    // ... existing code ...

    @RestController
    @RequestMapping("/api/interact")
    @Tag(name = "05-互动功能", description = "评分、打赏、举报相关接口")
    public class InteractController {

        @PostMapping("/rate")
        @Operation(summary = "给帖子评分",
                description = "对帖子进行评分（标签准确度1-5分 + 文章质量1-5分）\n\n" +
                        "【README格式】{\"action\":\"ratePost\",\"params\":{\"postId\":1,\"userId\":1,\"tagAccuracy\":4,\"articleScore\":5,\"comment\":\"评论\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "评分成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "评分成功",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "评分失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "您已经评分过了",
                            "data": null
                        }""")))
        })
        public String ratePost(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer userId = ((Number) params.get("userId")).intValue();
            Integer tagAccuracy = ((Number) params.get("tagAccuracy")).intValue();
            Integer articleScore = ((Number) params.get("articleScore")).intValue();
            String comment = (String) params.get("comment");
            Response<?> resp = ratingService.ratePost(postId, userId, tagAccuracy, articleScore, comment);
            return gson.toJson(resp);
        }

        @PostMapping("/tip")
        @Operation(summary = "打赏帖子",
                description = "打赏帖子，平台抽成8%\n\n" +
                        "【README格式】{\"action\":\"tipPost\",\"params\":{\"postId\":1,\"fromUserId\":1,\"amount\":10}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "打赏成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "打赏成功",
                            "data": {
                                "tipAmount": 10.00,
                                "platformFee": 0.80,
                                "authorReceive": 9.20
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "打赏失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "余额不足",
                            "data": null
                        }""")))
        })
        public String tipPost(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer fromUserId = ((Number) params.get("fromUserId")).intValue();
            BigDecimal amount = new BigDecimal(params.get("amount").toString());
            Response<?> resp = tipService.tipPost(postId, fromUserId, amount);
            return gson.toJson(resp);
        }

        @PostMapping("/report")
        @Operation(summary = "举报内容",
                description = "举报违规的帖子或回复\n\n" +
                        "【README格式】{\"action\":\"report\",\"params\":{\"reporterId\":1,\"targetType\":1,\"targetId\":1,\"reason\":\"举报原因\"}}\n\n" +
                        "targetType=1表示帖子，2表示回复")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "举报成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "举报成功，我们会尽快处理",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "举报失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "您已经举报过了",
                            "data": null
                        }""")))
        })
        public String report(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer reporterId = ((Number) params.get("reporterId")).intValue();
            Integer targetType = ((Number) params.get("targetType")).intValue();
            Integer targetId = ((Number) params.get("targetId")).intValue();
            String reason = (String) params.get("reason");
            Response<?> resp = reportService.report(reporterId, targetType, targetId, reason);
            return gson.toJson(resp);
        }
    }

    @RestController
    @RequestMapping("/api/task")
    @Tag(name = "06-每日任务", description = "互动任务功能相关接口")
    public class DailyTaskController {

        @PostMapping("/today")
        @Operation(summary = "获取今日互动任务",
                description = "获取今日的续写任务或其他互动任务\n\n" +
                        "【README格式】{\"action\":\"getTodayTask\",\"params\":{}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
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
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "今日暂无任务",
                            "data": null
                        }""")))
        })
        public String getTodayTask(@RequestBody(required = false) Map<String, Object> request) {
            Response<?> resp = taskService.getTodayTask();
            return gson.toJson(resp);
        }

        @PostMapping("/submit")
        @Operation(summary = "提交任务回答",
                description = "提交今日任务的回答内容，每个用户每天只能提交一次\n\n" +
                        "【README格式】{\"action\":\"submitTaskAnswer\",\"params\":{\"taskId\":1,\"userId\":1,\"content\":\"回答内容\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "提交成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "提交成功",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "提交失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "您今天已经提交过了",
                            "data": null
                        }""")))
        })
        public String submitTaskAnswer(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer taskId = ((Number) params.get("taskId")).intValue();
            Integer userId = ((Number) params.get("userId")).intValue();
            String content = (String) params.get("content");
            Response<?> resp = taskService.submitAnswer(taskId, userId, content);
            return gson.toJson(resp);
        }

        @PostMapping("/top-answers")
        @Operation(summary = "获取任务优质回答",
                description = "获取指定任务的前N名高分优质回答\n\n" +
                        "【README格式】{\"action\":\"getTopAnswers\",\"params\":{\"taskId\":1,\"limit\":3}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "获取成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": [
                                {
                                    "answerId": 1,
                                    "userName": "李四",
                                    "content": "优秀的回答内容...",
                                    "score": 4.8,
                                    "submitTime": "2025-05-26T15:00:00"
                                }
                            ]
                        }"""))),
                @ApiResponse(responseCode = "1", description = "获取失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "暂无优质回答",
                            "data": []
                        }""")))
        })
        public String getTopAnswers(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer taskId = ((Number) params.get("taskId")).intValue();
            Integer limit = params.get("limit") != null ? ((Number) params.get("limit")).intValue() : 3;
            Response<?> resp = taskService.getTopAnswers(taskId, limit);
            return gson.toJson(resp);
        }

        @PostMapping("/check-submitted")
        @Operation(summary = "检查是否已提交今日任务",
                description = "检查用户今日是否已经提交过任务\n\n" +
                        "【README格式】{\"action\":\"hasSubmitted\",\"params\":{\"taskId\":1,\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "查询成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "success",
                            "data": {
                                "submitted": true,
                                "submitTime": "2025-05-26T10:00:00"
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "查询失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "查询失败",
                            "data": null
                        }""")))
        })
        public String hasSubmitted(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer taskId = ((Number) params.get("taskId")).intValue();
            Integer userId = ((Number) params.get("userId")).intValue();
            Response<?> resp = taskService.hasSubmitted(taskId, userId);
            return gson.toJson(resp);
        }
    }

    // ... existing code ...

    @RestController
    @RequestMapping("/api/admin")
    @Tag(name = "07-管理员功能", description = "帖子审核、回复审核、举报处理、用户管理相关接口")
    public class AdminController {

        @PostMapping("/post/approve")
        @Operation(summary = "通过帖子审核",
                description = "管理员审核通过帖子，帖子正式发布\n\n" +
                        "【README格式】{\"action\":\"approvePost\",\"params\":{\"postId\":1,\"adminId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "审核通过",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "帖子审核通过",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "审核失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "帖子不存在或已审核",
                            "data": null
                        }""")))
        })
        public String approvePost(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer adminId = ((Number) params.get("adminId")).intValue();
            Response<?> resp = postService.approvePost(postId, adminId);
            return gson.toJson(resp);
        }

        @PostMapping("/post/reject")
        @Operation(summary = "拒绝帖子审核",
                description = "管理员拒绝帖子审核，需填写拒绝原因\n\n" +
                        "【README格式】{\"action\":\"rejectPost\",\"params\":{\"postId\":1,\"adminId\":1,\"reason\":\"拒绝原因\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "审核拒绝",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "帖子已拒绝",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "审核失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "帖子不存在或已审核",
                            "data": null
                        }""")))
        })
        public String rejectPost(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer postId = ((Number) params.get("postId")).intValue();
            Integer adminId = ((Number) params.get("adminId")).intValue();
            String reason = (String) params.get("reason");
            Response<?> resp = postService.rejectPost(postId, adminId, reason);
            return gson.toJson(resp);
        }

        @PostMapping("/reply/approve")
        @Operation(summary = "通过回复审核",
                description = "管理员审核通过回复\n\n" +
                        "【README格式】{\"action\":\"approveReply\",\"params\":{\"replyId\":1,\"adminId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "审核通过",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "回复审核通过",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "审核失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "回复不存在或已审核",
                            "data": null
                        }""")))
        })
        public String approveReply(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer replyId = ((Number) params.get("replyId")).intValue();
            Integer adminId = ((Number) params.get("adminId")).intValue();
            Response<?> resp = replyService.approveReply(replyId, adminId);
            return gson.toJson(resp);
        }

        @PostMapping("/reply/reject")
        @Operation(summary = "拒绝回复审核",
                description = "管理员拒绝回复审核\n\n" +
                        "【README格式】{\"action\":\"rejectReply\",\"params\":{\"replyId\":1,\"adminId\":1,\"reason\":\"拒绝原因\"}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "审核拒绝",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "回复已拒绝",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "审核失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "回复不存在或已审核",
                            "data": null
                        }""")))
        })
        public String rejectReply(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer replyId = ((Number) params.get("replyId")).intValue();
            Integer adminId = ((Number) params.get("adminId")).intValue();
            String reason = (String) params.get("reason");
            Response<?> resp = replyService.rejectReply(replyId, adminId, reason);
            return gson.toJson(resp);
        }

        @PostMapping("/report/handle")
        @Operation(summary = "处理举报",
                description = "管理员处理举报内容\n\n" +
                        "【README格式】{\"action\":\"handleReport\",\"params\":{\"reportId\":1,\"handlerId\":1,\"action\":1,\"note\":\"处理备注\"}}\n\n" +
                        "action=1撤下并警告，2仅警告，3驳回举报")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "处理成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "举报处理完成",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "处理失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "举报不存在或已处理",
                            "data": null
                        }""")))
        })
        public String handleReport(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer reportId = ((Number) params.get("reportId")).intValue();
            Integer handlerId = ((Number) params.get("handlerId")).intValue();
            Integer action = ((Number) params.get("action")).intValue();
            String note = (String) params.get("note");
            Response<?> resp = reportService.handleReport(reportId, handlerId, action, note);
            return gson.toJson(resp);
        }

        @PostMapping("/user/warning")
        @Operation(summary = "警告用户",
                description = "警告用户，警告3次自动封禁\n\n" +
                        "【README格式】{\"action\":\"addWarning\",\"params\":{\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "警告成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "警告成功，当前警告次数：1",
                            "data": {
                                "warningCount": 1
                            }
                        }"""))),
                @ApiResponse(responseCode = "1", description = "警告失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户不存在",
                            "data": null
                        }""")))
        })
        public String addWarning(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            Response<?> resp = userService.addWarning(userId);
            return gson.toJson(resp);
        }

        @PostMapping("/user/ban")
        @Operation(summary = "封禁用户",
                description = "封禁用户账号，封禁后无法发布帖子/回复\n\n" +
                        "【README格式】{\"action\":\"banUser\",\"params\":{\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "封禁成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "用户已封禁",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "封禁失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户不存在或已被封禁",
                            "data": null
                        }""")))
        })
        public String banUser(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
            Response<?> resp = userService.banUser(userId);
            return gson.toJson(resp);
        }

        @PostMapping("/user/unban")
        @Operation(summary = "解封用户",
                description = "解除用户封禁状态\n\n" +
                        "【README格式】{\"action\":\"unbanUser\",\"params\":{\"userId\":1}}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "0", description = "解封成功",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 0,
                            "msg": "用户已解封",
                            "data": null
                        }"""))),
                @ApiResponse(responseCode = "1", description = "解封失败",
                        content = @Content(mediaType = "application/json",
                                examples = @ExampleObject(value = """
                        {
                            "code": 1,
                            "msg": "用户不存在或未被封禁",
                            "data": null
                        }""")))
        })
        public String unbanUser(@RequestBody Map<String, Object> request) {
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            Integer userId = ((Number) params.get("userId")).intValue();
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
        @ApiResponse(responseCode = "0", description = "服务正常",
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
        @ApiResponse(responseCode = "0", description = "获取成功",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                    {
                        "code": 0,
                        "msg": "success",
                        "data": {
                            "name": "陌言服务端",
                            "version": "2.1-SNAPSHOT",
                            "description": "文学社交平台服务端",
                            "apiDocs": "/api/public/docs"
                        }
                    }""")))
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
