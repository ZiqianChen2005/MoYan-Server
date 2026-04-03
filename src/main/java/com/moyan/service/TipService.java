package com.moyan.service;

import com.moyan.dto.Response;
import java.math.BigDecimal;

public interface TipService {
    Response<Void> tipPost(Integer postId, Integer fromUserId, BigDecimal amount);
}