package br.com.setebit.vendasml.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenEntity {
    @Id
    @Column(name = "user_id", length = 20)
    private String userId;
    
    @Column(name = "access_token", length = 500, nullable = false)
    private String accessToken;
    
    @Column(name = "refresh_token", length = 500, nullable = false)
    private String refreshToken;
    
    @Column(name = "expires_in")
    private Long expiresIn;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null && expiresIn != null) {
            expiresAt = LocalDateTime.now().plusSeconds(expiresIn);
        }
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean needsRefresh(long thresholdSeconds) {
        if (expiresAt == null) return true;
        LocalDateTime threshold = expiresAt.minusSeconds(thresholdSeconds);
        return LocalDateTime.now().isAfter(threshold);
    }
}

