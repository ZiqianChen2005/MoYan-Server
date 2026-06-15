package com.moyan.service;

import com.moyan.dto.Response;
import com.moyan.dto.ReplyDTO;
import java.util.List;
import java.util.Map;

public interface ReplyService {
    Response<Integer> createReply(Integer postId, Integer userId, Boolean isAnonymous, String content);
    Response<List<ReplyDTO>> getReplies(Integer postId, Integer page);
    Response<Void> approveReply(Integer replyId, Integer adminId);
    Response<Void> rejectReply(Integer replyId, Integer adminId, String reason);
    Response<Map<String, Object>> getRepliesByStatus(Integer status, Integer page, Integer size, String keyword, Integer postId);
    Response<Void> batchAudit(String replyIds, Integer status, Integer adminId, String note);
    Response<Map<String, Object>> getReplyDetail(Integer replyId);
    Response<List<Map<String, Object>>> getPostTitles();
}