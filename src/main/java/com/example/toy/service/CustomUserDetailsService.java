package com.example.toy.service;

import com.example.toy.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public Account loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountService.findByUserId(username);
        if (optionalAccount.isEmpty()) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        return optionalAccount.get();
    }
}