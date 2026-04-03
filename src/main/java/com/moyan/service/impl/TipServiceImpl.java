package com.moyan.service.impl;

import com.moyan.dao.*;
import com.moyan.dao.impl.*;
import com.moyan.dto.Response;
import com.moyan.entity.*;
import com.moyan.service.TipService;
import java.math.BigDecimal;

public class TipServiceImpl implements TipService {
    
    private TipDao tipDao = new TipDaoImpl();
    private PostDao postDao = new PostDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.08"); // 8%抽成
    
    @Override
    public Response<Void> tipPost(Integer postId, Integer fromUserId, BigDecimal amount) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.fail("打赏金额必须大于0");
        }
        
        // 检查帖子是否存在
        Post post = postDao.findByPostId(postId);
        if (post == null) {
            return Response.fail("帖子不存在");
        }
        
        // 不能给自己打赏
        if (post.getUserId().equals(fromUserId)) {
            return Response.fail("不能给自己打赏");
        }
        
        // 检查打赏用户是否存在且未被封禁
        User fromUser = userDao.findByUserId(fromUserId);
        if (fromUser == null || fromUser.getIsBanned()) {
            return Response.fail("账号异常");
        }
        
        // 计算平台抽成和作者收入
        BigDecimal platformFee = amount.multiply(PLATFORM_FEE_RATE);
        BigDecimal authorIncome = amount.subtract(platformFee);
        
        Tip tip = new Tip();
        tip.setFromUserId(fromUserId);
        tip.setToUserId(post.getUserId());
        tip.setPostId(postId);
        tip.setAmount(amount);
        tip.setPlatformFee(platformFee);
        tip.setAuthorIncome(authorIncome);
        
        int result = tipDao.insert(tip);
        if (result > 0) {
            return Response.success(null);
        }
        return Response.fail("打赏失败");
    }
}