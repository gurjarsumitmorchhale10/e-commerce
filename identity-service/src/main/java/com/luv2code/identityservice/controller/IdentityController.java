package com.luv2code.identityservice.controller;

import com.luv2code.identityservice.entity.dto.UserCredentialDto;
import com.luv2code.identityservice.service.IdentityService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class IdentityController {

    private final IdentityService userCredentialService;
    private final IdentityService identityService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    void createUser(@RequestBody UserCredentialDto userCredential) {
        userCredentialService.saveUserCredential(userCredential);
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    String getToken(@RequestBody UserCredentialDto userCredential) {
        return identityService.getToken(userCredential);
    }

    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    Claims validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return identityService.validateToken(token);
    }
}
