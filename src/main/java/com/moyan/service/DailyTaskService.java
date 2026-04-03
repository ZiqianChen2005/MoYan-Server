package com.moyan.service;

import com.moyan.dto.Response;
import com.moyan.entity.DailyTask;
import com.moyan.entity.TaskAnswer;
import java.util.List;

public interface DailyTaskService {
    Response<DailyTask> getTodayTask();
    Response<Integer> submitAnswer(Integer taskId, Integer userId, String content);
    Response<List<TaskAnswer>> getTopAnswers(Integer taskId, Integer limit);
    Response<Boolean> hasSubmitted(Integer taskId, Integer userId);
}