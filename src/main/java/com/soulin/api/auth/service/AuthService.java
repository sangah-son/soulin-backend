package com.soulin.api.auth.service;

import com.soulin.api.auth.dto.LoginRequest;
import com.soulin.api.auth.dto.LoginResponse;
import com.soulin.api.auth.dto.SignupRequest;
import com.soulin.api.auth.dto.SignupResponse;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String encodedPassword=passwordEncoder.encode(request.getPassword());
        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getUserName()
        );
        User savedUser = userRepository.save(user);
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUserName()
        );
    }

    @Transactional(readOnly=true)
    public LoginResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("이메일 혹의 비밀번호가 올바르지 않습니다."));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 올바르지 않습니다.");
        }
        return new LoginResponse(
                user.getId(),
                user.getUserName()
        );
    }
}
