package com.moyan.dao;

import com.moyan.entity.Post;
import java.util.List;

public interface PostDao {
    Post findByPostId(Integer postId);
    int insert(Post post);
    int updateStatus(Integer postId, Integer status, String rejectReason);
    int updateViewCount(Integer postId);
    int updateScore(Integer postId, Double totalScore);
    List<Post> findPendingList(int page, int size);
    List<Post> findApprovedList(int page, int size, String tag, String keyword);
    List<Post> findRecommendedList(int page, int size, int currentUserId);
    List<Post> findByUserId(Integer userId, int page, int size);
    int countByUserId(Integer userId);
    int countPending();
    int countByStatus(Integer status);
}