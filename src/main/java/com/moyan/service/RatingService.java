package com.moyan.service;

import com.moyan.dto.Response;

public interface RatingService {
    Response<Void> ratePost(Integer postId, Integer userId, Integer tagAccuracy, 
                            Integer articleScore, String comment);
}