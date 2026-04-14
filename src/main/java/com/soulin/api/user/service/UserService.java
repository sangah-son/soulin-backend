package com.soulin.api.user.service;

import com.soulin.api.user.dto.*;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        return new UpdatePasswordResponse("비밀번호가 성공적으로 변경되었습니다.");
    }
}
