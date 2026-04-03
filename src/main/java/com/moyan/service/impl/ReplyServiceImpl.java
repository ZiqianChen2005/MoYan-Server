package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.dto.Response;
import com.moyan.dto.ReplyDTO;
import com.moyan.entity.*;
import com.moyan.service.ReplyService;
import com.moyan.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class ReplyServiceImpl implements ReplyService {
    
    private ReplyDao replyDao = new ReplyDaoImpl();
    private AnonymousMappingDao anonymousDao = new AnonymousMappingDaoImpl();
    private PostDao postDao = new PostDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    
    @Override
    public Response<Integer> createReply(Integer postId, Integer userId, Boolean isAnonymous, String content) {
        if (StringUtil.isEmpty(content) || content.length() > 1000) {
            return Response.fail("回复内容不能为空且不超过1000字");
        }
        
        // 检查帖子是否存在
        Post post = postDao.findByPostId(postId);
        if (post == null) {
            return Response.fail("帖子不存在");
        }
        
        // 检查用户是否被封禁
        User user = userDao.findByUserId(userId);
        if (user == null || user.getIsBanned()) {
            return Response.fail("账号异常，无法回复");
        }
        
        Reply reply = new Reply();
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setIsAnonymous(isAnonymous);
        reply.setContent(content);
        
        // 处理匿名编号
        if (isAnonymous) {
            AnonymousMapping mapping = anonymousDao.findByPostAndUser(postId, userId);
            if (mapping != null) {
                reply.setAnonymousNum(mapping.getAnonymousNum());
            } else {
                int nextNum = anonymousDao.getNextAnonymousNum(postId);
                reply.setAnonymousNum(nextNum);
                
                AnonymousMapping newMapping = new AnonymousMapping();
                newMapping.setPostId(postId);
                newMapping.setUserId(userId);
                newMapping.setAnonymousNum(nextNum);
                anonymousDao.insert(newMapping);
            }
        }
        
        int replyId = replyDao.insert(reply);
        if (replyId > 0) {
            return Response.success(replyId);
        }
        return Response.fail("回复失败");
    }
    
    @Override
    public Response<List<ReplyDTO>> getReplies(Integer postId, Integer page) {
        if (page == null || page < 1) page = 1;
        int size = 20;
        
        List<Reply> replies = replyDao.findByPostId(postId, page, size);
        List<ReplyDTO> result = new ArrayList<>();
        
        for (Reply reply : replies) {
            ReplyDTO dto = new ReplyDTO();
            dto.setReplyId(reply.getReplyId());
            dto.setContent(reply.getContent());
            dto.setReplyTime(reply.getReplyTime());
            dto.setIsAnonymous(reply.getIsAnonymous());
            
            if (reply.getIsAnonymous()) {
                dto.setAuthorName("匿名用户" + (reply.getAnonymousNum() != null ? " #" + reply.getAnonymousNum() : ""));
                dto.setAnonymousNum(reply.getAnonymousNum());
            } else {
                dto.setAuthorName(reply.getAuthorNickname());
            }
            
            result.add(dto);
        }
        
        return Response.success(result);
    }
    
    @Override
    public Response<Void> approveReply(Integer replyId, Integer adminId) {
        int result = replyDao.updateStatus(replyId, 1);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("审核失败");
    }
    
    @Override
    public Response<Void> rejectReply(Integer replyId, Integer adminId, String reason) {
        int result = replyDao.updateStatus(replyId, 2);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("审核失败");
    }
}