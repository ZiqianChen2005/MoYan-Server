package com.moyan.service;

import com.moyan.dto.Response;
import java.util.Map;

public interface ReportService {
    Response<Void> report(Integer reporterId, Integer targetType, Integer targetId, String reason);
    Response<Void> handleReport(Integer reportId, Integer handlerId, Integer action, String note);

    // 新增方法
    Response<Map<String, Object>> getReportList(Integer status, Integer page, Integer size, Integer targetType);
    Response<Map<String, Object>> getReportDetail(Integer reportId);
}