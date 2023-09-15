package com.example.toy.config;

import com.example.toy.domain.Account;
import com.example.toy.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    private MessageSourceAccessor messages;

    @Autowired
    private AccountService accountService;

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        // 비밀번호 확인
        Account userDetails = accountService.authenticate(username, password);

        // 예외 발생 여부 확인
        preAuthenticationChecks(userDetails);

        return new UsernamePasswordAuthenticationToken(username, password, authentication.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void preAuthenticationChecks(Account userDetails) {
        try {
            userDetailsChecker.check(userDetails);
        } catch (LockedException e) {
            log.info("계정 잠금");
            throw e;
        } catch (DisabledException e) {
            log.info("계정 유효 만료");
            throw e;
        } catch (AccountExpiredException e) {
            log.info("계정 유효기한 만료");
            throw e;
        } catch (CredentialsExpiredException e) {
            log.info("비밀번호 기한 만료");
        } catch (UsernameNotFoundException e) {
            log.info("계정 없음");
            throw e;
        }
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {

    }
}