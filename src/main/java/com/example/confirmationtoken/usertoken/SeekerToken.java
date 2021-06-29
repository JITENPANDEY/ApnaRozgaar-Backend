package com.example.confirmationtoken.usertoken;

import com.example.entities.Company;
import com.example.entities.SeekerSignup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity

public class SeekerToken {

    @SequenceGenerator(
            name = "seeker_token_sequence",
            sequenceName = "seeker_token_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seeker_token_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "token_seeker_id"
    )
    private SeekerSignup seekerSignup;

    public SeekerToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, SeekerSignup seekerSignup) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.seekerSignup = seekerSignup;
    }


}
