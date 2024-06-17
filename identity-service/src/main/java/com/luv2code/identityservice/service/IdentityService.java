package com.luv2code.identityservice.service;

import com.luv2code.identityservice.entity.Authority;
import com.luv2code.identityservice.entity.UserAuthority;
import com.luv2code.identityservice.entity.UserCredential;
import com.luv2code.identityservice.entity.dto.UserCredentialDto;
import com.luv2code.identityservice.exception.InvalidCredentialsException;
import com.luv2code.identityservice.repository.AuthorityRepository;
import com.luv2code.identityservice.repository.IdentityRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final AuthorityRepository authorityRepository;

    private final JWTService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    private static final Logger logger = LoggerFactory.getLogger(IdentityService.class);


    public void saveUserCredential(UserCredentialDto userCredentialDto) {

        Optional<Authority> authority = authorityRepository.findByName("USER");

        UserCredential credential = UserCredential.builder()
                .username(userCredentialDto.username())
                .password(passwordEncoder.encode(userCredentialDto.password()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .credentialsNonExpired(true)
                .build();

        authority.ifPresent(userAuthority -> credential.setUserAuthorities(
                Set.of(UserAuthority.builder()
                        .authority(authority.get())
                        .userCredential(credential)
                        .build()
                )
        ));

        UserCredential saved = identityRepository.save(credential);
        logger.info("User credentials {} saved successfully!", saved.getId());
    }

    public String getToken(UserCredentialDto userCredential) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredential.username(), userCredential.password()));
        if (authenticate.isAuthenticated())
            return jwtService.generateToken(userCredential.username(), authenticate.getAuthorities());
        else throw new InvalidCredentialsException("Invalid credentials");
    }

    public Claims validateToken(String token) {
        return jwtService.validateToken(token);
    }
}
