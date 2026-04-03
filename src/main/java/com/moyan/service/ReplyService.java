package com.moyan.service;

import com.moyan.dto.Response;
import com.moyan.dto.ReplyDTO;
import java.util.List;

public interface ReplyService {
    Response<Integer> createReply(Integer postId, Integer userId, Boolean isAnonymous, String content);
    Response<List<ReplyDTO>> getReplies(Integer postId, Integer page);
    Response<Void> approveReply(Integer replyId, Integer adminId);
    Response<Void> rejectReply(Integer replyId, Integer adminId, String reason);
}