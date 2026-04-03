package com.moyan.dao;

import com.moyan.entity.AnonymousMapping;

public interface AnonymousMappingDao {
    AnonymousMapping findByPostAndUser(Integer postId, Integer userId);
    int insert(AnonymousMapping mapping);
    int getNextAnonymousNum(Integer postId);
}