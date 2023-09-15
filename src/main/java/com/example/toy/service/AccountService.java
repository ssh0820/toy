package com.example.toy.service;

import com.example.toy.domain.Account;
import com.example.toy.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Account> findByUserId(String username) {
        return accountRepository.findByUserId(username);
    }

    @Transactional
    public Account saveOrUpdate(Account account) {
        return accountRepository.save(account);
    }


    public Account authenticate(String username, String password) {
        Optional<Account> optionalAccount = accountRepository.findByUserId(username);
        if(optionalAccount.isPresent()) {
            log.info("@Authenticate : PW 확인");
            Account account = optionalAccount.get();
            if(passwordMatches(account, password)){
                if(!account.isCredentialsNonExpired())
                    unlockedUser(account);
            } else{
                throw new BadCredentialsException("Wrong password");
            }
        } else{
            throw new UsernameNotFoundException("[" + username + "] Username Not Found.");
        }

        return optionalAccount.get();

    }

    public void updateLoginInfo(Account account) {
        log.info("@loginSuccess : 로그인 정보 업데이트");
        account.setLoginFailCnt(0);
        account.setLastLoginDate(LocalDate.now());
        account.setAccountExpiredDate(LocalDate.now().plusYears(1));
        accountRepository.save(account);
    }

    private boolean passwordMatches(Account account, String password) {
        if (!passwordEncoder.matches(password, account.getPassword())) {
            increaseFailureCount(account);
            int cnt = getFailureCount(account);
            if(cnt > 4) {
                lockedUser(account);
            }
            return false;
        }
        return true;
    }

    public int getFailureCount(Account account) {
        return account.getLoginFailCnt();
    }

    private void increaseFailureCount(Account account) {
        account.setLoginFailCnt(account.getLoginFailCnt()+1);
        accountRepository.save(account);
    }

    private void lockedUser(Account account) {
        account.setAccountNonLocked(false);
        accountRepository.save(account);
    }

    private void unlockedUser(Account account) {
        account.setAccountNonLocked(true);
        accountRepository.save(account);
    }


}