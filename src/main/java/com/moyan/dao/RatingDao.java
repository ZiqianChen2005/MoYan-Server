package com.moyan.dao;

import com.moyan.entity.Rating;
import java.util.List;

public interface RatingDao {
    Rating findByPostAndUser(Integer postId, Integer userId);
    int insert(Rating rating);
    List<Rating> findByPostId(Integer postId);
    double getAvgTagAccuracy(Integer postId);
    double getAvgArticleScore(Integer postId);
    int countByPostId(Integer postId);
}