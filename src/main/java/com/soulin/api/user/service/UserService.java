package com.soulin.api.user.service;

import com.soulin.api.auth.repository.RefreshTokenRepository;
import com.soulin.api.bookmark.repository.BookmarkRepository;
import com.soulin.api.moderation.repository.ModerationRepository;
import com.soulin.api.mypage.repository.DailyRepresentativePostRepository;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.reaction.repository.PostReactionRepository;
import com.soulin.api.user.dto.*;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final DailyRepresentativePostRepository dailyRepresentativePostRepository;
    private final ModerationRepository moderationRepository;

    @Transactional(readOnly=true)
    public ProfileResponse getMyProfile(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new ProfileResponse(
                user.getEmail(),
                user.getUserName()
        );
    }

    public UpdateProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updateProfile(request.getEmail(), request.getUserName());
        return new UpdateProfileResponse(
                user.getEmail(),
                user.getUserName()
        );
    }

    public UpdatePasswordResponse updateMyPassword(Long userId, UpdatePasswordRequest request){
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if(!passwordEncoder.matches(request.getCurrentPassword(),user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if(!request.getNewPassword().equals(request.getNewPasswordConfirm())){
            throw new IllegalArgumentException("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        user.increaseTokenVersion();
        refreshTokenRepository.revokeAllByUserId(userId);
        return new UpdatePasswordResponse("비밀번호가 성공적으로 변경되었습니다.");
    }

    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 내가 누른 북마크/리액션 삭제
        bookmarkRepository.deleteAllByUser(user);
        postReactionRepository.deleteAllByUser(user);

        // 내 포스트에 달린 북마크/리액션/대표글 삭제 후 포스트 삭제
        List<Post> myPosts = postRepository.findAllByUser(user);
        for (Post post : myPosts) {
            bookmarkRepository.deleteAllByPost(post);
            postReactionRepository.deleteAllByPost(post);
            dailyRepresentativePostRepository.deleteAllByPost(post);
            moderationRepository.deleteAllByPost(post);
        }
        dailyRepresentativePostRepository.deleteAllByUser(user);
        postRepository.deleteAll(myPosts);

        // 리프레시토큰 삭제 후 유저 삭제
        refreshTokenRepository.deleteAllByUserId(userId);
        userRepository.delete(user);
    }
}
