package com.soulin.api.user.entity;

import com.soulin.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "token_version", nullable = false)
    private Long tokenVersion = 0L;

    public User(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    public void updateProfile(String email, String userName){
        if(email!=null){
            this.email=email;
        }
        if(userName!=null) {
            this.userName = userName;
        }
    }
    public void updatePassword(String password){
        this.password=password;
    }

    public void increaseTokenVersion() {
        this.tokenVersion++;
    }
}
