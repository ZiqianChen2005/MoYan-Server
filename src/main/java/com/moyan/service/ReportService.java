package com.moyan.service;

import com.moyan.dto.Response;

public interface ReportService {
    Response<Void> report(Integer reporterId, Integer targetType, Integer targetId, String reason);
    Response<Void> handleReport(Integer reportId, Integer handlerId, Integer action, String note);
}