package com.moyan.dao;

import com.moyan.entity.Reply;
import java.util.List;

public interface ReplyDao {
    Reply findByReplyId(Integer replyId);
    int insert(Reply reply);
    int updateStatus(Integer replyId, Integer status);
    List<Reply> findByPostId(Integer postId, int page, int size);
    List<Reply> findPendingList(int page, int size);
    int countByPostId(Integer postId);
    int countPending();
    int getAnonymousCount(Integer postId, Integer userId);
    // 在 ReplyDao.java 中添加
    List<Reply> findByStatus(Integer status, int page, int size, String keyword, Integer postId);
    int countByStatus(Integer status, String keyword, Integer postId);
}