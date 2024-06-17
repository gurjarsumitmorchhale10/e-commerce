package com.luv2code.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserCredential userCredential;


    @Override
    public String getAuthority() {
        return authority.getName();
    }
}
