package com.moyan.dao;

import com.moyan.entity.Tip;
import java.math.BigDecimal;
import java.util.List;

public interface TipDao {
    int insert(Tip tip);
    List<Tip> findByToUserId(Integer toUserId, int page, int size);
    List<Tip> findByPostId(Integer postId);
    BigDecimal getTotalTipAmountByPostId(Integer postId);
    BigDecimal getTotalTipAmountByUserId(Integer userId);
}