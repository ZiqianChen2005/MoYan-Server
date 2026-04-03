package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.dto.Response;
import com.moyan.dto.PostListDTO;
import com.moyan.dto.PostDetailDTO;
import com.moyan.dto.ReplyDTO;
import com.moyan.entity.*;
import com.moyan.service.PostService;
import com.moyan.util.StringUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PostServiceImpl implements PostService {
    
    private PostDao postDao = new PostDaoImpl();
    private ReplyDao replyDao = new ReplyDaoImpl();
    private RatingDao ratingDao = new RatingDaoImpl();
    private TipDao tipDao = new TipDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    
    @Override
    public Response<Integer> createPost(Integer userId, Boolean isAnonymous, String title, 
                                         String content, String tags) {
        // 参数校验
        if (StringUtil.isEmpty(title) || title.length() > 100) {
            return Response.fail("标题不能为空且不超过100字");
        }
        if (StringUtil.isEmpty(content)) {
            return Response.fail("内容不能为空");
        }
        
        // 检查用户是否被封禁
        User user = userDao.findByUserId(userId);
        if (user == null || user.getIsBanned()) {
            return Response.fail("账号异常，无法发帖");
        }
        
        // 检查是否是新人（前5篇帖子）
        int userPostCount = postDao.countByUserId(userId);
        boolean isNewbie = userPostCount < 5;
        
        Post post = new Post();
        post.setUserId(userId);
        post.setIsAnonymous(isAnonymous);
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        post.setIsNewbie(isNewbie);
        
        int postId = postDao.insert(post);
        if (postId > 0) {
            return Response.success(postId);
        }
        return Response.fail("发帖失败");
    }
    
    @Override
    public Response<List<PostListDTO>> getPostList(Integer page, Integer size, Integer currentUserId) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;
        
        List<Post> posts = postDao.findRecommendedList(page, size, currentUserId);
        List<PostListDTO> result = new ArrayList<>();
        
        for (Post post : posts) {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setTitle(post.getTitle());
            dto.setContentPreview(StringUtil.truncate(post.getContent(), 100));
            dto.setTags(post.getTags());
            dto.setPostTime(post.getPostTime());
            dto.setViewCount(post.getViewCount());
            dto.setIsNewbie(post.getIsNewbie());
            dto.setTotalScore(post.getTotalScore());
            
            // 获取回复数
            int replyCount = replyDao.countByPostId(post.getPostId());
            dto.setReplyCount(replyCount);
            
            // 处理作者信息
            if (post.getIsAnonymous()) {
                dto.setAuthorName("匿名用户");
                dto.setIsAnonymous(true);
            } else {
                dto.setAuthorName(post.getAuthorNickname());
                dto.setIsAnonymous(false);
            }
            
            result.add(dto);
        }
        
        return Response.success(result);
    }
    
    @Override
    public Response<PostDetailDTO> getPostDetail(Integer postId, Integer currentUserId) {
        Post post = postDao.findByPostId(postId);
        if (post == null) {
            return Response.fail("帖子不存在");
        }
        
        // 增加浏览量
        postDao.updateViewCount(postId);
        
        PostDetailDTO dto = new PostDetailDTO();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setTags(post.getTags());
        dto.setPostTime(post.getPostTime());
        dto.setViewCount(post.getViewCount() + 1);
        dto.setIsNewbie(post.getIsNewbie());
        
        // 处理作者信息
        if (post.getIsAnonymous()) {
            dto.setAuthorName("匿名用户");
            dto.setIsAnonymous(true);
        } else {
            dto.setAuthorName(post.getAuthorNickname());
            dto.setIsAnonymous(false);
        }
        
        // 计算综合评分 (文章评分55% + tag精确度30% + 打赏量15%)
        double avgArticleScore = ratingDao.getAvgArticleScore(postId);
        double avgTagAccuracy = ratingDao.getAvgTagAccuracy(postId);
        BigDecimal totalTip = tipDao.getTotalTipAmountByPostId(postId);
        double tipScore = totalTip.doubleValue() * 0.15;
        double totalScore = avgArticleScore * 0.55 + avgTagAccuracy * 0.3 + tipScore;
        dto.setTotalScore(totalScore);
        
        // 获取回复数
        int replyCount = replyDao.countByPostId(postId);
        dto.setReplyCount(replyCount);
        
        // 检查当前用户是否已评分
        if (currentUserId != null && currentUserId > 0) {
            Rating userRating = ratingDao.findByPostAndUser(postId, currentUserId);
            if (userRating != null) {
                dto.setCanUserRate(false);
                dto.setUserRatingTag(userRating.getTagAccuracy());
                dto.setUserRatingArticle(userRating.getArticleScore());
            } else {
                dto.setCanUserRate(true);
            }
        } else {
            dto.setCanUserRate(true);
        }
        
        // 获取回复列表
        List<Reply> replies = replyDao.findByPostId(postId, 1, 100);
        List<ReplyDTO> replyDTOs = new ArrayList<>();
        for (Reply reply : replies) {
            ReplyDTO replyDTO = new ReplyDTO();
            replyDTO.setReplyId(reply.getReplyId());
            replyDTO.setContent(reply.getContent());
            replyDTO.setReplyTime(reply.getReplyTime());
            replyDTO.setIsAnonymous(reply.getIsAnonymous());
            
            if (reply.getIsAnonymous()) {
                replyDTO.setAuthorName("匿名用户" + (reply.getAnonymousNum() != null ? " #" + reply.getAnonymousNum() : ""));
                replyDTO.setAnonymousNum(reply.getAnonymousNum());
            } else {
                replyDTO.setAuthorName(reply.getAuthorNickname());
            }
            
            replyDTOs.add(replyDTO);
        }
        dto.setReplies(replyDTOs);
        
        return Response.success(dto);
    }
    
    @Override
    public Response<List<PostListDTO>> searchPosts(String keyword, String tag, String sortBy, Integer page) {
        if (page == null || page < 1) page = 1;
        int size = 20;
        
        List<Post> posts = postDao.findApprovedList(page, size, tag, keyword);
        List<PostListDTO> result = new ArrayList<>();
        
        for (Post post : posts) {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setTitle(post.getTitle());
            dto.setContentPreview(StringUtil.truncate(post.getContent(), 100));
            dto.setTags(post.getTags());
            dto.setPostTime(post.getPostTime());
            dto.setViewCount(post.getViewCount());
            
            int replyCount = replyDao.countByPostId(post.getPostId());
            dto.setReplyCount(replyCount);
            
            if (post.getIsAnonymous()) {
                dto.setAuthorName("匿名用户");
                dto.setIsAnonymous(true);
            } else {
                dto.setAuthorName(post.getAuthorNickname());
                dto.setIsAnonymous(false);
            }
            
            result.add(dto);
        }
        
        return Response.success(result);
    }
    
    @Override
    public Response<Void> approvePost(Integer postId, Integer adminId) {
        int result = postDao.updateStatus(postId, 1, null);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("审核失败");
    }
    
    @Override
    public Response<Void> rejectPost(Integer postId, Integer adminId, String reason) {
        int result = postDao.updateStatus(postId, 2, reason);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("审核失败");
    }
}