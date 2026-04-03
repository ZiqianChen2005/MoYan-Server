package com.moyan.dao;

import com.moyan.entity.DailyTask;
import java.util.Date;
import java.util.List;

public interface DailyTaskDao {
    DailyTask findByDate(Date date);
    int insert(DailyTask task);
    int update(DailyTask task);
    List<DailyTask> findAll(int page, int size);
    DailyTask findByTaskId(Integer taskId);
}