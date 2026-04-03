package com.moyan.dao;

import com.moyan.entity.Report;
import java.util.List;

public interface ReportDao {
    int insert(Report report);
    int updateStatus(Integer reportId, Integer status, Integer handlerId, String handleNote);
    List<Report> findByStatus(Integer status, int page, int size);
    Report findByReportId(Integer reportId);
    int countByStatus(Integer status);
}