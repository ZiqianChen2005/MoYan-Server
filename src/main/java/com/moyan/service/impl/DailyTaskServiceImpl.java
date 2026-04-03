package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.dto.Response;
import com.moyan.entity.*;
import com.moyan.service.DailyTaskService;
import com.moyan.util.StringUtil;
import java.util.Date;
import java.util.List;

public class DailyTaskServiceImpl implements DailyTaskService {
    
    private DailyTaskDao taskDao = new DailyTaskDaoImpl();
    private TaskAnswerDao answerDao = new TaskAnswerDaoImpl();
    
    @Override
    public Response<DailyTask> getTodayTask() {
        DailyTask task = taskDao.findByDate(new Date());
        if (task == null) {
            return Response.fail("今日暂无互动任务");
        }
        return Response.success(task);
    }
    
    @Override
    public Response<Integer> submitAnswer(Integer taskId, Integer userId, String content) {
        if (StringUtil.isEmpty(content)) {
            return Response.fail("回答内容不能为空");
        }
        
        // 检查是否已提交
        if (answerDao.hasSubmitted(taskId, userId)) {
            return Response.fail("您已经提交过今日任务的回答了");
        }
        
        TaskAnswer answer = new TaskAnswer();
        answer.setTaskId(taskId);
        answer.setUserId(userId);
        answer.setContent(content);
        
        int answerId = answerDao.insert(answer);
        if (answerId > 0) {
            return Response.success(answerId);
        }
        return Response.fail("提交失败");
    }
    
    @Override
    public Response<List<TaskAnswer>> getTopAnswers(Integer taskId, Integer limit) {
        if (limit == null || limit < 1) limit = 3;
        List<TaskAnswer> answers = answerDao.findByTaskId(taskId, limit);
        return Response.success(answers);
    }
    
    @Override
    public Response<Boolean> hasSubmitted(Integer taskId, Integer userId) {
        boolean submitted = answerDao.hasSubmitted(taskId, userId);
        return Response.success(submitted);
    }
}