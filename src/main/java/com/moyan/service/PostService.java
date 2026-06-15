package com.moyan.service;

import com.moyan.dto.Response;
import com.moyan.dto.PostListDTO;
import com.moyan.dto.PostDetailDTO;
import java.util.List;
import java.util.Map;

public interface PostService {
    Response<Integer> createPost(Integer userId, Boolean isAnonymous, String title, 
                                  String content, String tags);
    Response<List<PostListDTO>> getPostList(Integer page, Integer size, Integer currentUserId);
    Response<PostDetailDTO> getPostDetail(Integer postId, Integer currentUserId);
    Response<List<PostListDTO>> searchPosts(String keyword, String tag, String sortBy, Integer page);
    Response<Void> approvePost(Integer postId, Integer adminId);
    Response<Void> rejectPost(Integer postId, Integer adminId, String reason);
    Response<List<PostListDTO>> getPostsByUserId(Integer userId, Integer page, Integer size);
    // 在 PostService.java 中添加
    Response<Map<String, Object>> getPostsByStatus(Integer status, Integer page, Integer size, String tag, String keyword);
    Response<Integer> getPendingCount();
}
