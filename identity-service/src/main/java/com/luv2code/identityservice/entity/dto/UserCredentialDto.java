package com.luv2code.identityservice.entity.dto;

import java.io.Serializable;

/**
 * DTO for {@link com.luv2code.identityservice.entity.UserCredential}
 */
public record UserCredentialDto(String username, String password) implements Serializable {
}