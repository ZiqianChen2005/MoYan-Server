package com.moyan.dao;

import com.moyan.entity.TaskAnswer;
import java.util.List;

public interface TaskAnswerDao {
    int insert(TaskAnswer answer);
    int updateScore(Integer answerId, Integer score);
    List<TaskAnswer> findByTaskId(Integer taskId, int limit);
    List<TaskAnswer> findByUserId(Integer userId, int page, int size);
    boolean hasSubmitted(Integer taskId, Integer userId);
}