package com.moyan.controller;

import com.google.gson.Gson;
import com.moyan.dto.Response;
import com.moyan.service.PostService;
import com.moyan.service.ReplyService;
import com.moyan.service.ReportService;
import com.moyan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@Tag(name = "举报管理", description = "举报相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private UserService userService;

    private final Gson gson = new Gson();

    /**
     * 获取举报列表（管理员）
     */
    @PostMapping("/admin/list")
    @Operation(summary = "获取举报列表")
    public String getReportList(@RequestBody Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 0;
        Integer page = params.get("page") != null ? ((Number) params.get("page")).intValue() : 1;
        Integer size = params.get("size") != null ? ((Number) params.get("size")).intValue() : 10;
        Integer targetType = params.get("targetType") != null ? ((Number) params.get("targetType")).intValue() : null;

        Response<?> resp = reportService.getReportList(status, page, size, targetType);
        return gson.toJson(resp);
    }

    /**
     * 获取举报详情
     */
    @PostMapping("/admin/detail")
    @Operation(summary = "获取举报详情")
    public String getReportDetail(@RequestBody Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        Integer reportId = ((Number) params.get("reportId")).intValue();

        Response<?> resp = reportService.getReportDetail(reportId);
        return gson.toJson(resp);
    }

    /**
     * 处理举报（管理员）
     */
    @PostMapping("/admin/handle")
    @Operation(summary = "处理举报")
    public String handleReport(@RequestBody Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        Integer reportId = ((Number) params.get("reportId")).intValue();
        Integer handlerId = ((Number) params.get("handlerId")).intValue();
        Integer action = ((Number) params.get("action")).intValue();
        String note = (String) params.get("note");

        Response<?> resp = reportService.handleReport(reportId, handlerId, action, note);
        return gson.toJson(resp);
    }
}