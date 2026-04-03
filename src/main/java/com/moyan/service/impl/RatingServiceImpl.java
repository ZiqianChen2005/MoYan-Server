package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.service.RatingService;
import com.moyan.dto.Response;
import com.moyan.entity.*;

public class RatingServiceImpl implements RatingService {
    
    private RatingDao ratingDao = new RatingDaoImpl();
    private PostDao postDao = new PostDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    
    @Override
    public Response<Void> ratePost(Integer postId, Integer userId, Integer tagAccuracy, 
                                    Integer articleScore, String comment) {
        // 参数校验
        if (tagAccuracy < 1 || tagAccuracy > 5) {
            return Response.fail("Tag准确度评分必须在1-5之间");
        }
        if (articleScore < 1 || articleScore > 5) {
            return Response.fail("文章评分必须在1-5之间");
        }
        
        // 检查帖子是否存在
        Post post = postDao.findByPostId(postId);
        if (post == null) {
            return Response.fail("帖子不存在");
        }
        
        // 检查用户是否已评分
        Rating existing = ratingDao.findByPostAndUser(postId, userId);
        if (existing != null) {
            return Response.fail("您已经对该帖子评过分了");
        }
        
        // 不能给自己的帖子评分
        if (post.getUserId().equals(userId)) {
            return Response.fail("不能给自己的帖子评分");
        }
        
        Rating rating = new Rating();
        rating.setPostId(postId);
        rating.setUserId(userId);
        rating.setTagAccuracy(tagAccuracy);
        rating.setArticleScore(articleScore);
        rating.setComment(comment);
        
        int result = ratingDao.insert(rating);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("评分失败");
    }
}