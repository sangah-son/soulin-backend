package com.soulin.api.post.service;

import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.post.PostStatus;
import com.soulin.api.post.dto.CreatePostRequest;
import com.soulin.api.post.dto.MyPostSummaryResponse;
import com.soulin.api.post.dto.PostDetailResponse;
import com.soulin.api.post.dto.UpdatePostRequest;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ColorRepository colorRepository;

    public PostDetailResponse createDraftPost(Long userId, CreatePostRequest request){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Color color=colorRepository.findById(request.getColorId())
                .orElseThrow(()->new IllegalArgumentException("색상을 찾을 수 없습니다."));
        Post post=new Post(
                request.getTitle(),
                request.getContent(),
                request.getIsPublic(),
                PostStatus.DRAFT,
                user,
                color
        );
        Post savedPost=postRepository.save(post);
        return new PostDetailResponse(
                savedPost.getPostId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getIsPublic(),
                savedPost.getColor().getColorId(),
                savedPost.getUser().getUserName(),
                savedPost.getStatus(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<MyPostSummaryResponse> getMyPosts(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<Post> posts=postRepository.findAllByUser(user);

        return posts.stream()
                .map(post->new MyPostSummaryResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getIsPublic(),
                        post.getStatus(),
                        post.getColor().getColorId(),
                        post.getUser().getUserName(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ))
                .toList();
    }

    //피드, 마이페이지 detail 공용 service
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId){
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getIsPublic(),
                post.getColor().getColorId(),
                post.getUser().getUserName(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public PostDetailResponse updatePost(Long postId, UpdatePostRequest request){
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Color color=colorRepository.findById(request.getColorId())
                .orElseThrow(()->new IllegalArgumentException("색상을 찾을 수 없습니다."));
        post.updatePost(
                request.getTitle(),
                request.getContent(),
                request.getIsPublic(),
                color
        );
        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getIsPublic(),
                post.getColor().getColorId(),
                post.getUser().getUserName(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public void deletePost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        postRepository.delete(post);
    }
}
