package com.luv2code.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luv2code.identityservice.entity.Authority;
import com.luv2code.identityservice.entity.UserAuthority;
import com.luv2code.identityservice.entity.UserCredential;
import com.luv2code.identityservice.entity.dto.UserCredentialDto;
import com.luv2code.identityservice.exception.InvalidCredentialsException;
import com.luv2code.identityservice.repository.AuthorityRepository;
import com.luv2code.identityservice.repository.IdentityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {IdentityService.class, PasswordEncoder.class, AuthenticationManager.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class IdentityServiceTest {
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthorityRepository authorityRepository;

    @MockBean
    private IdentityRepository identityRepository;

    @Autowired
    private IdentityService identityService;

    @MockBean
    private JWTService jWTService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    /**
     * Method under test:
     * {@link IdentityService#saveUserCredential(UserCredentialDto)}
     */
    @Test
    void testSaveUserCredential() {

        // Arrange
        UserCredential userCredential = new UserCredential();
        userCredential.setAccountNonExpired(true);
        userCredential.setAccountNonLocked(true);
        userCredential.setAuthoritySet(new HashSet<>());
        userCredential.setCredentialsNonExpired(true);
        userCredential.setEnabled(true);
        userCredential.setId(1L);
        userCredential.setPassword("password");
        userCredential.setUserAuthorities(new HashSet<>());
        userCredential.setUsername("janedoe");
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        when(identityRepository.save(Mockito.<UserCredential>any())).thenReturn(userCredential);

        Authority authority = new Authority();
        authority.setId(1);
        authority.setName("Name");
        Optional<Authority> ofResult = Optional.of(authority);
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        when(authorityRepository.findByName(Mockito.<String>any())).thenReturn(ofResult);

        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        JWTService jwtService = new JWTService();
        IdentityService identityService = new IdentityService(identityRepository, authorityRepository, jwtService,
                new BCryptPasswordEncoder(), authenticationManager);

        // Act
        identityService.saveUserCredential(new UserCredentialDto("janedoe", "password"));

        // Assert
        verify(authorityRepository).findByName(eq("USER"));
        verify(identityRepository).save(isA(UserCredential.class));
    }

    /**
     * Method under test:
     * {@link IdentityService#saveUserCredential(UserCredentialDto)}
     */
    @Test
    void testSaveUserCredential2() {

        // Arrange
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        when(authorityRepository.findByName(Mockito.<String>any()))
                .thenThrow(new InvalidCredentialsException("An error occurred"));

        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        JWTService jwtService = new JWTService();
        IdentityService identityService = new IdentityService(identityRepository, authorityRepository, jwtService,
                new BCryptPasswordEncoder(), authenticationManager);

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> identityService.saveUserCredential(new UserCredentialDto("janedoe", "password")));
        verify(authorityRepository).findByName(eq("USER"));
    }

    /**
     * Method under test:
     * {@link IdentityService#saveUserCredential(UserCredentialDto)}
     */
    @Test
    void testSaveUserCredential3() {

        // Arrange
        UserCredential userCredential = mock(UserCredential.class);
        when(userCredential.getId()).thenReturn(1L);
        doNothing().when(userCredential).setAccountNonExpired(anyBoolean());
        doNothing().when(userCredential).setAccountNonLocked(anyBoolean());
        doNothing().when(userCredential).setAuthoritySet(Mockito.<Set<UserAuthority>>any());
        doNothing().when(userCredential).setCredentialsNonExpired(anyBoolean());
        doNothing().when(userCredential).setEnabled(anyBoolean());
        doNothing().when(userCredential).setId(Mockito.<Long>any());
        doNothing().when(userCredential).setPassword(Mockito.<String>any());
        doNothing().when(userCredential).setUserAuthorities(Mockito.<Set<UserAuthority>>any());
        doNothing().when(userCredential).setUsername(Mockito.<String>any());
        userCredential.setAccountNonExpired(true);
        userCredential.setAccountNonLocked(true);
        userCredential.setAuthoritySet(new HashSet<>());
        userCredential.setCredentialsNonExpired(true);
        userCredential.setEnabled(true);
        userCredential.setId(1L);
        userCredential.setPassword("password");
        userCredential.setUserAuthorities(new HashSet<>());
        userCredential.setUsername("janedoe");
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        when(identityRepository.save(Mockito.<UserCredential>any())).thenReturn(userCredential);

        Authority authority = new Authority();
        authority.setId(1);
        authority.setName("Name");
        Optional<Authority> ofResult = Optional.of(authority);
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        when(authorityRepository.findByName(Mockito.<String>any())).thenReturn(ofResult);

        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        JWTService jwtService = new JWTService();
        IdentityService identityService = new IdentityService(identityRepository, authorityRepository, jwtService,
                new BCryptPasswordEncoder(), authenticationManager);

        // Act
        identityService.saveUserCredential(new UserCredentialDto("janedoe", "password"));

        // Assert
        verify(userCredential).getId();
        verify(userCredential).setAccountNonExpired(eq(true));
        verify(userCredential).setAccountNonLocked(eq(true));
        verify(userCredential).setAuthoritySet(isA(Set.class));
        verify(userCredential).setCredentialsNonExpired(eq(true));
        verify(userCredential).setEnabled(eq(true));
        verify(userCredential).setId(eq(1L));
        verify(userCredential).setPassword(eq("password"));
        verify(userCredential).setUserAuthorities(isA(Set.class));
        verify(userCredential).setUsername(eq("janedoe"));
        verify(authorityRepository).findByName(eq("USER"));
        verify(identityRepository).save(isA(UserCredential.class));
    }

    /**
     * Method under test:
     * {@link IdentityService#saveUserCredential(UserCredentialDto)}
     */
    @Test
    void testSaveUserCredential4() {

        // Arrange
        UserCredential userCredential = mock(UserCredential.class);
        when(userCredential.getId()).thenReturn(1L);
        doNothing().when(userCredential).setAccountNonExpired(anyBoolean());
        doNothing().when(userCredential).setAccountNonLocked(anyBoolean());
        doNothing().when(userCredential).setAuthoritySet(Mockito.<Set<UserAuthority>>any());
        doNothing().when(userCredential).setCredentialsNonExpired(anyBoolean());
        doNothing().when(userCredential).setEnabled(anyBoolean());
        doNothing().when(userCredential).setId(Mockito.<Long>any());
        doNothing().when(userCredential).setPassword(Mockito.<String>any());
        doNothing().when(userCredential).setUserAuthorities(Mockito.<Set<UserAuthority>>any());
        doNothing().when(userCredential).setUsername(Mockito.<String>any());
        userCredential.setAccountNonExpired(true);
        userCredential.setAccountNonLocked(true);
        userCredential.setAuthoritySet(new HashSet<>());
        userCredential.setCredentialsNonExpired(true);
        userCredential.setEnabled(true);
        userCredential.setId(1L);
        userCredential.setPassword("password");
        userCredential.setUserAuthorities(new HashSet<>());
        userCredential.setUsername("janedoe");
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        when(identityRepository.save(Mockito.<UserCredential>any())).thenReturn(userCredential);
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
        Optional<Authority> emptyResult = Optional.empty();
        when(authorityRepository.findByName(Mockito.<String>any())).thenReturn(emptyResult);

        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        JWTService jwtService = new JWTService();
        IdentityService identityService = new IdentityService(identityRepository, authorityRepository, jwtService,
                new BCryptPasswordEncoder(), authenticationManager);

        // Act
        identityService.saveUserCredential(new UserCredentialDto("janedoe", "password"));

        // Assert
        verify(userCredential).getId();
        verify(userCredential).setAccountNonExpired(eq(true));
        verify(userCredential).setAccountNonLocked(eq(true));
        verify(userCredential).setAuthoritySet(isA(Set.class));
        verify(userCredential).setCredentialsNonExpired(eq(true));
        verify(userCredential).setEnabled(eq(true));
        verify(userCredential).setId(eq(1L));
        verify(userCredential).setPassword(eq("password"));
        verify(userCredential).setUserAuthorities(isA(Set.class));
        verify(userCredential).setUsername(eq("janedoe"));
        verify(authorityRepository).findByName(eq("USER"));
        verify(identityRepository).save(isA(UserCredential.class));
    }

    /**
     * Method under test: {@link IdentityService#getToken(UserCredentialDto)}
     */
    @Test
    void testGetToken() throws AuthenticationException {
        // Arrange
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> identityService.getToken(new UserCredentialDto("janedoe", "password")));
        verify(authenticationManager).authenticate(isA(Authentication.class));
    }

    /**
     * Method under test: {@link IdentityService#getToken(UserCredentialDto)}
     */
    @Test
    void testGetToken2() throws AuthenticationException {
        // Arrange
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        Class<Authentication> originalAuthentication = Authentication.class;
        when(authenticationManager.authenticate(Mockito.<Authentication>any())).thenReturn(
                new RunAsUserToken("Invalid credentials", "Principal", "Credentials", authorities, originalAuthentication));
        when(jWTService.generateToken(Mockito.<String>any(), Mockito.<Collection<GrantedAuthority>>any()))
                .thenReturn("ABC123");

        // Act
        String actualToken = identityService.getToken(new UserCredentialDto("janedoe", "password"));

        // Assert
        verify(jWTService).generateToken(eq("janedoe"), isA(Collection.class));
        verify(authenticationManager).authenticate(isA(Authentication.class));
        assertEquals("ABC123", actualToken);
    }

    /**
     * Method under test: {@link IdentityService#getToken(UserCredentialDto)}
     */
    @Test
    void testGetToken3() throws AuthenticationException {
        // Arrange
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        Class<Authentication> originalAuthentication = Authentication.class;
        when(authenticationManager.authenticate(Mockito.<Authentication>any())).thenReturn(
                new RunAsUserToken("Invalid credentials", "Principal", "Credentials", authorities, originalAuthentication));
        when(jWTService.generateToken(Mockito.<String>any(), Mockito.<Collection<GrantedAuthority>>any()))
                .thenThrow(new InvalidCredentialsException("An error occurred"));

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> identityService.getToken(new UserCredentialDto("janedoe", "password")));
        verify(jWTService).generateToken(eq("janedoe"), isA(Collection.class));
        verify(authenticationManager).authenticate(isA(Authentication.class));
    }

    /**
     * Method under test: {@link IdentityService#validateToken(String)}
     */
    @Test
    void testValidateToken() {
        // Arrange
        JWTService jwtService = mock(JWTService.class);
        DefaultClaims defaultClaims = new DefaultClaims();
        when(jwtService.validateToken(Mockito.<String>any())).thenReturn(defaultClaims);

        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        AuthorityRepository authorityRepository = mock(AuthorityRepository.class);

        // Act
        Claims actualValidateTokenResult = (new IdentityService(identityRepository, authorityRepository, jwtService,
                new BCryptPasswordEncoder(), authenticationManager)).validateToken("ABC123");

        // Assert
        verify(jwtService).validateToken(eq("ABC123"));
        assertTrue(actualValidateTokenResult.isEmpty());
        assertSame(defaultClaims, actualValidateTokenResult);
    }

}
