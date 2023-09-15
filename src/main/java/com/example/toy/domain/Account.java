package com.example.toy.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account implements UserDetails, Serializable {

    private static final long serialVersionUID = -2088867218879056605L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 100, nullable = false)
    private String password;

    private String korName;

    @Column(length = 256)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.USER;

    @Column(name = "fail_cnt")
    private Integer loginFailCnt = 0;

    // 계정 잠김 여부
    private boolean accountNonLocked = true;

    // 사용 여부
    private boolean enabled = true;

    // 메일 수신 여부
    private boolean receiveEmail = false;

    // 회원 가입 일자
    private LocalDate joinDate = LocalDate.now();

    // 최근 로그인 일자
    private LocalDate lastLoginDate;

    // 1년 이상 로그인 하지 않을 시 계정 만료
    private LocalDate accountExpiredDate;

    // 3개월 마다 비밀번호 변경 필요
    private LocalDate credentialsExpiredDate;


    @Builder
    public Account(Long id, String userId, String password, String korName){
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.korName = korName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        return roles;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isFutureDate(accountExpiredDate);
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isFutureDate(credentialsExpiredDate);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    private boolean isFutureDate(LocalDate accountExpiredDate) {

        return false;
    }

}
