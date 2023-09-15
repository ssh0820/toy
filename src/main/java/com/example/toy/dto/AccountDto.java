package com.example.toy.dto;

import com.example.toy.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@Builder
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String userId;
    private String password;
    private String korName;

    public Account toEntity(){
        Account build = Account.builder()
                .id(id)
                .userId(userId)
                .password(new BCryptPasswordEncoder().encode(password))
                .korName(korName)
                .build();
        return build;
    }

}
