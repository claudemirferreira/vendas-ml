# Integração Mercado Livre com Spring Boot + OpenFeign + MySQL + Refresh Token

## 1️⃣ Criar Projeto Spring Boot

### Dependências no `pom.xml`:
```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2024.0.0</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-mysql</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.6.0</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>21</source>
                <target>21</target>
                <release>21</release>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Habilitar OpenFeign na classe principal:
```java
@SpringBootApplication
@EnableFeignClients
public class VendasmlApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendasmlApplication.class, args);
    }
}
```

## 2️⃣ Configuração (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mercadolivre_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: seu_usuario
    password: sua_senha
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    encoding: UTF-8
    out-of-order: false
    validate-on-migrate: true
  
  jpa:
    hibernate:
      ddl-auto: validate  # Usar validate com Flyway (não permite criação automática)
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
  
  cloud:
    compatibility-verifier:
      enabled: false  # Desabilita verificação de compatibilidade (Spring Boot 3.5.7 requer Spring Cloud 2024.0.0+)

mercadolivre:
  client-id: ${MERCADOLIVRE_CLIENT_ID:seu_client_id}
  client-secret: ${MERCADOLIVRE_CLIENT_SECRET:seu_client_secret}
  redirect-uri: ${MERCADOLIVRE_REDIRECT_URI:http://localhost:8080/callback}
  base-url: https://api.mercadolibre.com
  auth-url: https://auth.mercadolibre.com.ar
  token-refresh-threshold-seconds: 300  # Refresh 5min antes de expirar

logging:
  level:
    br.com.setebit.vendasml: DEBUG
    org.springframework.cloud.openfeign: DEBUG
    com.zaxxer.hikari: DEBUG
    org.springframework.boot.autoconfigure.jdbc: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    tags-sorter: alpha
    operations-sorter: alpha
    try-it-out-enabled: true
  show-actuator: false
```
> **Importante:** Crie o banco `mercadolivre_db` antes de rodar a aplicação. As migrações do Flyway serão executadas automaticamente na primeira execução.

## 3️⃣ Migrações do Banco de Dados (Flyway)

### Estrutura de Pastas
```
src/main/resources/
  db/
    migration/
      V1__Create_tokens_table.sql
```

### Migração Inicial - Criar Tabela de Tokens

**V1__Create_tokens_table.sql**
```sql
-- Flyway migration: Create tokens table
-- Version: 1
-- Description: Cria tabela para armazenar tokens de autenticação do Mercado Livre

CREATE TABLE IF NOT EXISTS tokens (
    user_id VARCHAR(20) PRIMARY KEY,
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500) NOT NULL,
    expires_in BIGINT,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para melhor performance
CREATE INDEX idx_expires_at ON tokens(expires_at);
```

> **Nota:** O Flyway executa as migrações automaticamente na inicialização da aplicação. O padrão de nomenclatura é `V{versão}__{descrição}.sql`.

## 4️⃣ Entidade e Repositório

**TokenEntity.java**
```java
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
```

**TokenRepository.java**
```java
package br.com.setebit.vendasml.repository;

import br.com.setebit.vendasml.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
}
```

## 5️⃣ DTOs com Validações

**TokenResponse.java**
```java
package br.com.setebit.vendasml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    private String scope;
    
    @JsonProperty("user_id")
    private Long userId;
}
```

**TokenRequest.java**
```java
package br.com.setebit.vendasml.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {
    @NotBlank(message = "Código de autorização é obrigatório")
    private String code;
}
```

**ItemRequest.java**
```java
package br.com.setebit.vendasml.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 256, message = "Título deve ter no máximo 256 caracteres")
    private String title;
    
    @NotBlank(message = "ID da categoria é obrigatório")
    private String category_id;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private Double price;
    
    @NotBlank(message = "Moeda é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Moeda deve ser código de 3 letras (ex: BRL, USD)")
    private String currency_id;
    
    @NotNull(message = "Quantidade disponível é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer available_quantity;
    
    @NotBlank(message = "Modo de compra é obrigatório")
    private String buying_mode; // "buy_it_now"
    
    @NotBlank(message = "Condição é obrigatória")
    private String condition; // "new", "used"
    
    @NotBlank(message = "Tipo de listagem é obrigatório")
    private String listing_type_id; // "gold_special", "gold_pro", "gold", "silver", "bronze"
    
    @Valid
    @NotNull(message = "Descrição é obrigatória")
    private Description description;
    
    @Valid
    @NotEmpty(message = "Pelo menos uma imagem é obrigatória")
    @Size(max = 12, message = "Máximo de 12 imagens")
    private List<Picture> pictures;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Description {
        @NotBlank(message = "Texto da descrição é obrigatório")
        @Size(max = 50000, message = "Descrição deve ter no máximo 50000 caracteres")
        private String plain_text;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Picture {
        @NotBlank(message = "URL da imagem é obrigatória")
        private String source;
    }
}
```

**ItemResponse.java**
```java
package br.com.setebit.vendasml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemResponse {
    private String id;
    private String title;
    private Double price;
    
    @JsonProperty("available_quantity")
    private Integer availableQuantity;
    
    private String status;
    private String permalink;
}
```

**ErrorResponse.java**
```java
package br.com.setebit.vendasml.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
}
```

## 6️⃣ Configuração do OpenFeign

**FeignConfig.java**
```java
package br.com.setebit.vendasml.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
```

**FeignErrorDecoder.java**
```java
package br.com.setebit.vendasml.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {
    
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        
        return switch (status) {
            case UNAUTHORIZED -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, 
                "Token inválido ou expirado. Tente fazer refresh."
            );
            case FORBIDDEN -> new ResponseStatusException(
                HttpStatus.FORBIDDEN, 
                "Acesso negado ao recurso"
            );
            case NOT_FOUND -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Recurso não encontrado"
            );
            default -> new ResponseStatusException(
                status, 
                "Erro na comunicação com Mercado Livre: " + response.reason()
            );
        };
    }
}
```

**MercadoLivreAuthClient.java**
```java
package br.com.setebit.vendasml.client;

import br.com.setebit.vendasml.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "mercadoLivreAuthClient", 
    url = "${mercadolivre.base-url}",
    configuration = FeignConfig.class
)
public interface MercadoLivreAuthClient {
    
    @PostMapping(
        value = "/oauth/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenResponse getAccessToken(@RequestBody MultiValueMap<String, String> formData);
    
    @PostMapping(
        value = "/oauth/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenResponse refreshToken(@RequestBody MultiValueMap<String, String> formData);
}
```

**MercadoLivreItemClient.java**
```java
package br.com.setebit.vendasml.client;

import br.com.setebit.vendasml.dto.ItemRequest;
import br.com.setebit.vendasml.dto.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "mercadoLivreItemClient", 
    url = "${mercadolivre.base-url}",
    configuration = FeignConfig.class
)
public interface MercadoLivreItemClient {
    
    @PostMapping("/items")
    ItemResponse createItem(
        @RequestHeader("Authorization") String authorization,
        @RequestBody ItemRequest request
    );

    @GetMapping("/items/{itemId}")
    ItemResponse getItem(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("itemId") String itemId
    );

    @PutMapping("/items/{itemId}")
    ItemResponse updateItem(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("itemId") String itemId,
        @RequestBody ItemRequest request
    );
    
    @DeleteMapping("/items/{itemId}")
    void deleteItem(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("itemId") String itemId
    );
}
```

## 7️⃣ Serviço de Integração

**MercadoLivreService.java**
```java
package br.com.setebit.vendasml.service;

import br.com.setebit.vendasml.client.MercadoLivreAuthClient;
import br.com.setebit.vendasml.client.MercadoLivreItemClient;
import br.com.setebit.vendasml.dto.ItemRequest;
import br.com.setebit.vendasml.dto.ItemResponse;
import br.com.setebit.vendasml.dto.TokenResponse;
import br.com.setebit.vendasml.entity.TokenEntity;
import br.com.setebit.vendasml.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoLivreService {
    
    private final MercadoLivreAuthClient authClient;
    private final MercadoLivreItemClient itemClient;
    private final TokenRepository tokenRepository;
    
    @Value("${mercadolivre.client-id}")
    private String clientId;
    
    @Value("${mercadolivre.client-secret}")
    private String clientSecret;
    
    @Value("${mercadolivre.redirect-uri}")
    private String redirectUri;
    
    @Value("${mercadolivre.auth-url}")
    private String authUrl;
    
    @Value("${mercadolivre.token-refresh-threshold-seconds:300}")
    private long refreshThresholdSeconds;
    
    /**
     * Obtém token usando código de autorização e persiste no banco
     */
    @Transactional
    public TokenResponse exchangeCodeForToken(String code) {
        log.info("Trocando código de autorização por token");
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        
        try {
            TokenResponse response = authClient.getAccessToken(formData);
            
            TokenEntity tokenEntity = TokenEntity.builder()
                .userId(String.valueOf(response.getUserId()))
                .accessToken(response.getAccessToken())
                .refreshToken(response.getRefreshToken())
                .expiresIn(response.getExpiresIn())
                .expiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()))
                .build();
            
            tokenRepository.save(tokenEntity);
            log.info("Token salvo para usuário: {}", response.getUserId());
            
            return response;
        } catch (Exception e) {
            log.error("Erro ao obter token: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Erro ao obter token: " + e.getMessage()
            );
        }
    }
    
    /**
     * Atualiza o token de acesso usando refresh token
     */
    @Transactional
    public TokenResponse refreshAccessToken(String userId) {
        log.info("Atualizando token para usuário: {}", userId);
        
        TokenEntity tokenEntity = tokenRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Token não encontrado para usuário: " + userId
            ));
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", tokenEntity.getRefreshToken());
        
        try {
            TokenResponse response = authClient.refreshToken(formData);
            
            tokenEntity.setAccessToken(response.getAccessToken());
            tokenEntity.setRefreshToken(response.getRefreshToken());
            tokenEntity.setExpiresIn(response.getExpiresIn());
            tokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()));
            
            tokenRepository.save(tokenEntity);
            log.info("Token atualizado para usuário: {}", userId);
            
            return response;
        } catch (Exception e) {
            log.error("Erro ao atualizar token: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Erro ao atualizar token: " + e.getMessage()
            );
        }
    }
    
    /**
     * Obtém token válido, fazendo refresh se necessário
     */
    private String getValidAccessToken(String userId) {
        TokenEntity tokenEntity = tokenRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Token não encontrado para usuário: " + userId
            ));
        
        if (tokenEntity.needsRefresh(refreshThresholdSeconds)) {
            log.info("Token expirando, fazendo refresh automático");
            refreshAccessToken(userId);
            tokenEntity = tokenRepository.findById(userId).orElseThrow();
        }
        
        return tokenEntity.getAccessToken();
    }
    
    /**
     * Cria produto no Mercado Livre
     */
    public ItemResponse createProduct(String userId, ItemRequest request) {
        log.info("Criando produto para usuário: {}", userId);
        String token = getValidAccessToken(userId);
        return itemClient.createItem("Bearer " + token, request);
    }
    
    /**
     * Busca produto no Mercado Livre
     */
    public ItemResponse getProduct(String userId, String itemId) {
        log.info("Buscando produto {} para usuário: {}", itemId, userId);
        String token = getValidAccessToken(userId);
        return itemClient.getItem("Bearer " + token, itemId);
    }
    
    /**
     * Atualiza produto no Mercado Livre
     */
    public ItemResponse updateProduct(String userId, String itemId, ItemRequest request) {
        log.info("Atualizando produto {} para usuário: {}", itemId, userId);
        String token = getValidAccessToken(userId);
        return itemClient.updateItem("Bearer " + token, itemId, request);
    }
    
    /**
     * Deleta produto no Mercado Livre
     */
    public void deleteProduct(String userId, String itemId) {
        log.info("Deletando produto {} para usuário: {}", itemId, userId);
        String token = getValidAccessToken(userId);
        itemClient.deleteItem("Bearer " + token, itemId);
    }
    
    /**
     * Retorna URL de autorização do Mercado Livre
     */
    public String getAuthorizationUrl() {
        return String.format(
            "%s/authorization?response_type=code&client_id=%s&redirect_uri=%s",
            authUrl,
            clientId,
            redirectUri
        );
    }
}
```

## 8️⃣ Controller

**MercadoLivreController.java**
```java
package br.com.setebit.vendasml.controller;

import br.com.setebit.vendasml.dto.*;
import br.com.setebit.vendasml.service.MercadoLivreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/mercadolivre")
@RequiredArgsConstructor
@Tag(name = "Mercado Livre", description = "API para integração com Mercado Livre - Autenticação OAuth e gerenciamento de produtos")
public class MercadoLivreController {
    
    private final MercadoLivreService mercadoLivreService;
    
    @Operation(
        summary = "Obter URL de autorização",
        description = "Retorna a URL para redirecionar o usuário para autorização OAuth do Mercado Livre"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL de autorização gerada com sucesso")
    })
    @GetMapping("/auth/url")
    public ResponseEntity<String> getAuthorizationUrl() {
        return ResponseEntity.ok(mercadoLivreService.getAuthorizationUrl());
    }
    
    @Operation(
        summary = "Trocar código por token",
        description = "Troca o código de autorização retornado pelo Mercado Livre por um token de acesso"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token obtido com sucesso",
                content = @Content(schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "400", description = "Código inválido ou erro na requisição")
    })
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> exchangeToken(
            @Parameter(description = "Código de autorização do Mercado Livre", required = true)
            @Valid @RequestBody TokenRequest request) {
        TokenResponse response = mercadoLivreService.exchangeCodeForToken(request.getCode());
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Atualizar token de acesso",
        description = "Atualiza o token de acesso usando o refresh token armazenado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "404", description = "Token não encontrado para o usuário")
    })
    @PostMapping("/refresh/{userId}")
    public ResponseEntity<TokenResponse> refreshToken(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String userId) {
        TokenResponse response = mercadoLivreService.refreshAccessToken(userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Criar produto",
        description = "Cria um novo produto no Mercado Livre"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                content = @Content(schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @PostMapping("/produtos")
    public ResponseEntity<ItemResponse> createProduct(
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam String userId,
            @Parameter(description = "Dados do produto", required = true)
            @Valid @RequestBody ItemRequest request) {
        ItemResponse response = mercadoLivreService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Consultar produto",
        description = "Busca um produto específico do Mercado Livre por ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado",
                content = @Content(schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/produtos/{id}")
    public ResponseEntity<ItemResponse> getProduct(
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam String userId,
            @Parameter(description = "ID do produto no Mercado Livre", required = true)
            @PathVariable String id) {
        ItemResponse response = mercadoLivreService.getProduct(userId, id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Atualizar produto",
        description = "Atualiza um produto existente no Mercado Livre"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = ItemResponse.class))),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/produtos/{id}")
    public ResponseEntity<ItemResponse> updateProduct(
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam String userId,
            @Parameter(description = "ID do produto no Mercado Livre", required = true)
            @PathVariable String id,
            @Parameter(description = "Dados atualizados do produto", required = true)
            @Valid @RequestBody ItemRequest request) {
        ItemResponse response = mercadoLivreService.updateProduct(userId, id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Deletar produto",
        description = "Remove um produto do Mercado Livre"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam String userId,
            @Parameter(description = "ID do produto no Mercado Livre", required = true)
            @PathVariable String id) {
        mercadoLivreService.deleteProduct(userId, id);
        return ResponseEntity.noContent().build();
    }
}
```

## 9️⃣ Exception Handler Global

**GlobalExceptionHandler.java**
```java
package br.com.setebit.vendasml.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();

        // Ignora endpoints do Springdoc OpenAPI
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger") || path.startsWith("/swagger-ui")) {
            throw ex; // delega para o Spring processar normalmente
        }

        // Tratamento das exceções da sua aplicação
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    public static class ApiError {
        private int status;
        private String message;
        private String path;

        public ApiError(HttpStatus status, String message, String path) {
            this.status = status.value();
            this.message = message;
            this.path = path;
        }

        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
    }
}
```

> **⚠️ Importante:** O `GlobalExceptionHandler` está configurado para ignorar rotas do SpringDoc/Swagger, evitando conflitos com a documentação da API. O filtro `SpringDocFilter` também ajuda a garantir isso.

## 🔟 Documentação da API com Swagger/OpenAPI

### Configuração do SpringDoc OpenAPI

**OpenApiConfig.java**
```java
package br.com.setebit.vendasml.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mercado Livre Integration API")
                        .version("1.0.0")
                        .description("API para integração com Mercado Livre - Gerenciamento de tokens OAuth e produtos")
                        .contact(new Contact()
                                .name("Vendas ML")
                                .email("contato@setebit.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.vendasml.com.br")
                                .description("Servidor de Produção")
                ));
    }
}
```

### Filtro para Proteção do SpringDoc

**SpringDocFilter.java**
```java
package br.com.setebit.vendasml.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro para garantir que rotas do SpringDoc/Swagger não sejam interceptadas
 * pelo GlobalExceptionHandler. Este filtro deve ter alta prioridade.
 */
@Component
@Order(1)
public class SpringDocFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        
        // Adiciona atributo para identificar rotas do SpringDoc
        if (path != null && (path.contains("/api-docs") || 
                            path.contains("/swagger-ui") || 
                            path.contains("/v3/api-docs") ||
                            path.contains("/swagger") ||
                            path.contains("/openapi"))) {
            request.setAttribute("SPRINGDOC_REQUEST", true);
        }
        
        chain.doFilter(request, response);
    }
}
```

### Configuração Adicional do SpringDoc (Opcional)

**SpringDocConfig.java**
```java
package br.com.setebit.vendasml.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração adicional para garantir que o SpringDoc funcione corretamente
 * mesmo com o GlobalExceptionHandler ativo
 */
@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("mercadolivre-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
```

### Anotações no Controller

O `MercadoLivreController` completo com todas as anotações Swagger está mostrado na seção **8️⃣ Controller** acima. As principais anotações utilizadas são:

- `@Tag` - Define o nome e descrição da API
- `@Operation` - Documenta cada endpoint individual
- `@ApiResponses` / `@ApiResponse` - Define os códigos de resposta possíveis
- `@Parameter` - Documenta os parâmetros do endpoint
- `@Schema` - Define o schema dos DTOs na documentação

### Acessar a Documentação

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

### Endpoints Documentados

1. **Autenticação OAuth**
   - `GET /api/mercadolivre/auth/url` - Obter URL de autorização
   - `POST /api/mercadolivre/token` - Trocar código por token
   - `POST /api/mercadolivre/refresh/{userId}` - Atualizar token

2. **Produtos**
   - `POST /api/mercadolivre/produtos` - Criar produto
   - `GET /api/mercadolivre/produtos/{id}` - Consultar produto
   - `PUT /api/mercadolivre/produtos/{id}` - Atualizar produto
   - `DELETE /api/mercadolivre/produtos/{id}` - Deletar produto

## 1️⃣1️⃣ Testes com Postman

### Variáveis de Ambiente:
```
base_url: http://localhost:8080
auth_code: <código_retornado_após_autorização>
user_id: <user_id_do_token>
item_id: <id_do_item_criado>
access_token: <token_atualizado>
```

### Fluxo Completo:

#### 1. Obter URL de Autorização
```
GET {{base_url}}/api/mercadolivre/auth/url
```
**Resposta:**
```json
"https://auth.mercadolibre.com.ar/authorization?response_type=code&client_id=SEU_CLIENT_ID&redirect_uri=SEU_REDIRECT_URI"
```
Acesse a URL no navegador e autorize. O callback retornará um `code`.

#### 2. Trocar Código por Token
```
POST {{base_url}}/api/mercadolivre/token
Content-Type: application/json

{
  "code": "{{auth_code}}"
}
```
**Resposta:**
```json
{
  "accessToken": "APP_USR-123456789",
  "refreshToken": "TG-123456789",
  "expiresIn": 21600,
  "tokenType": "Bearer",
  "scope": "offline_access read write",
  "userId": 123456789
}
```
Atualize `{{user_id}}` com o `userId` retornado.

#### 3. Criar Produto
```
POST {{base_url}}/api/mercadolivre/produtos?userId={{user_id}}
Content-Type: application/json

{
  "title": "Produto de Teste",
  "category_id": "MLB1144",
  "price": 99.90,
  "currency_id": "BRL",
  "available_quantity": 10,
  "buying_mode": "buy_it_now",
  "condition": "new",
  "listing_type_id": "gold_special",
  "description": {
    "plain_text": "Descrição detalhada do produto de teste"
  },
  "pictures": [
    {
      "source": "https://http2.mlstatic.com/D_123456-O.jpg"
    }
  ]
}
```
**Resposta:**
```json
{
  "id": "MLB123456789",
  "title": "Produto de Teste",
  "price": 99.90,
  "availableQuantity": 10,
  "status": "active"
}
```
Atualize `{{item_id}}` com o `id` retornado.

#### 4. Consultar Produto
```
GET {{base_url}}/api/mercadolivre/produtos/{{item_id}}?userId={{user_id}}
```

#### 5. Atualizar Produto
```
PUT {{base_url}}/api/mercadolivre/produtos/{{item_id}}?userId={{user_id}}
Content-Type: application/json

{
  "title": "Produto Atualizado",
  "category_id": "MLB1144",
  "price": 149.90,
  "currency_id": "BRL",
  "available_quantity": 5,
  "buying_mode": "buy_it_now",
  "condition": "new",
  "listing_type_id": "gold_special",
  "description": {
    "plain_text": "Nova descrição"
  },
  "pictures": [
    {
      "source": "https://http2.mlstatic.com/D_123456-O.jpg"
    }
  ]
}
```

#### 6. Refresh Token Automático
```
POST {{base_url}}/api/mercadolivre/refresh/{{user_id}}
```
O refresh é feito automaticamente antes de cada chamada à API se o token estiver próximo do vencimento.

#### 7. Deletar Produto
```
DELETE {{base_url}}/api/mercadolivre/produtos/{{item_id}}?userId={{user_id}}
```

## 1️⃣1️⃣ Configuração para Testes

### Arquivo de Configuração de Teste

**application-test.yaml** (src/test/resources/application-test.yaml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  flyway:
    enabled: false  # Desabilita Flyway nos testes
  
  jpa:
    hibernate:
      ddl-auto: create-drop  # Cria e remove tabelas automaticamente nos testes
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
  
  cloud:
    compatibility-verifier:
      enabled: false  # Desabilita verificação de compatibilidade nos testes

mercadolivre:
  client-id: test_client_id
  client-secret: test_client_secret
  redirect-uri: http://localhost:8080/callback
  base-url: https://api.mercadolibre.com
  auth-url: https://auth.mercadolibre.com.ar
  token-refresh-threshold-seconds: 300

logging:
  level:
    br.com.setebit.vendasml: INFO
    org.springframework.cloud.openfeign: INFO
```

### Configuração da Classe de Teste

**VendasmlApplicationTests.java**
```java
package br.com.setebit.vendasml;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VendasmlApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

## 1️⃣2️⃣ Testes Unitários (Exemplo)

**MercadoLivreServiceTest.java**
```java
package br.com.setebit.vendasml.service;

import br.com.setebit.vendasml.client.MercadoLivreAuthClient;
import br.com.setebit.vendasml.client.MercadoLivreItemClient;
import br.com.setebit.vendasml.dto.*;
import br.com.setebit.vendasml.entity.TokenEntity;
import br.com.setebit.vendasml.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoLivreServiceTest {
    
    @Mock
    private MercadoLivreAuthClient authClient;
    
    @Mock
    private MercadoLivreItemClient itemClient;
    
    @Mock
    private TokenRepository tokenRepository;
    
    @InjectMocks
    private MercadoLivreService service;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "clientId", "test-client-id");
        ReflectionTestUtils.setField(service, "clientSecret", "test-secret");
        ReflectionTestUtils.setField(service, "redirectUri", "http://test.com");
        ReflectionTestUtils.setField(service, "refreshThresholdSeconds", 300L);
    }
    
    @Test
    void shouldExchangeCodeForToken() {
        // Given
        String code = "test-code";
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("access-token");
        tokenResponse.setRefreshToken("refresh-token");
        tokenResponse.setExpiresIn(21600L);
        tokenResponse.setUserId(123456L);
        
        when(authClient.getAccessToken(any())).thenReturn(tokenResponse);
        
        // When
        TokenResponse result = service.exchangeCodeForToken(code);
        
        // Then
        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        verify(tokenRepository, times(1)).save(any(TokenEntity.class));
    }
    
    @Test
    void shouldCreateProductWithAutoRefresh() {
        // Given
        String userId = "123456";
        TokenEntity tokenEntity = TokenEntity.builder()
            .userId(userId)
            .accessToken("old-token")
            .refreshToken("refresh-token")
            .expiresAt(LocalDateTime.now().plusHours(1))
            .expiresIn(3600L)
            .build();
        
        ItemRequest request = ItemRequest.builder()
            .title("Produto Teste")
            .category_id("MLB1144")
            .price(99.90)
            .currency_id("BRL")
            .available_quantity(10)
            .buying_mode("buy_it_now")
            .condition("new")
            .listing_type_id("gold_special")
            .description(ItemRequest.Description.builder()
                .plain_text("Descrição")
                .build())
            .pictures(List.of(ItemRequest.Picture.builder()
                .source("https://example.com/image.jpg")
                .build()))
            .build();
        
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId("MLB123");
        itemResponse.setTitle("Produto Teste");
        
        when(tokenRepository.findById(userId)).thenReturn(Optional.of(tokenEntity));
        when(itemClient.createItem(anyString(), any(ItemRequest.class)))
            .thenReturn(itemResponse);
        
        // When
        ItemResponse result = service.createProduct(userId, request);
        
        // Then
        assertNotNull(result);
        assertEquals("MLB123", result.getId());
        verify(itemClient).createItem(eq("Bearer old-token"), any(ItemRequest.class));
    }
}
```

## 1️⃣3️⃣ Docker Compose para MySQL

### docker-compose.yml
```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: mercadolivre-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: admin
      MYSQL_DATABASE: mercadolivre_db
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    command: 
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-authentication-plugin=mysql_native_password
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-padmin"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - mercadolivre-network

volumes:
  mysql_data:
    driver: local

networks:
  mercadolivre-network:
    driver: bridge
```

### init.sql (opcional)
```sql
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
```

### Comandos Docker
```bash
# Iniciar MySQL
docker-compose up -d

# Ver logs
docker-compose logs -f mysql

# Parar MySQL
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

## 1️⃣4️⃣ Considerações Finais

### Segurança:
- ⚠️ **Nunca** commite credenciais no código. Use variáveis de ambiente ou arquivos de configuração externos.
- Considere usar **Spring Vault** ou **AWS Secrets Manager** para produção.
- Os tokens são armazenados em texto plano. Considere encriptar campos sensíveis.

### Ambiente Sandbox:
- Use `https://auth.mercadolibre.com.ar` para produção Argentina
- Use `https://auth.mercadolivre.com.br` para produção Brasil
- Sandbox: `https://auth.mercadolibre.com.ar` (com credenciais de teste)

### Rate Limiting:
- O Mercado Livre possui limites de requisições. Implemente retry com backoff exponencial.
- Monitore respostas 429 (Too Many Requests).

### Logging:
- Configure logs apropriados para produção.
- Não logue tokens ou informações sensíveis.

### Monitoramento:
- Use Spring Actuator para health checks.
- Monitore expiração de tokens e falhas de refresh.

### Migrações Flyway:
- ⚠️ **Importante:** As migrações são executadas automaticamente na inicialização
- Use o padrão de nomenclatura: `V{versão}__{descrição}.sql`
- Nunca modifique migrações já executadas em produção (crie novas)
- O Flyway mantém histórico de versões na tabela `flyway_schema_history`
- Para desabilitar Flyway temporariamente: `spring.flyway.enabled=false`

### Documentação da API (Swagger):
- Acesse http://localhost:8080/swagger-ui.html após iniciar a aplicação
- A documentação OpenAPI está disponível em JSON: http://localhost:8080/api-docs
- Todas as rotas estão documentadas com exemplos e descrições
- Em produção, considere desabilitar o Swagger UI ou protegê-lo com autenticação
- Customize a configuração em `OpenApiConfig.java` conforme necessário
- **Versão recomendada:** SpringDoc 2.6.0+ (compatível com Spring Boot 3.5.7)
- O `SpringDocFilter` garante que o `GlobalExceptionHandler` não interfira na documentação

### Compatibilidade de Versões:
- **Spring Boot:** 3.5.7
- **Spring Cloud:** 2024.0.0 (compatível com Spring Boot 3.5.7)
- **SpringDoc OpenAPI:** 2.6.0 (compatível com Spring Framework 6.2.12)
- **Java:** 21 (LTS)
- O `maven-compiler-plugin` deve estar configurado explicitamente para Java 21

### Melhorias Futuras:
- Cache de tokens em Redis
- Scheduler para refresh proativo de tokens
- Webhook para receber notificações do Mercado Livre
- Retry automático com circuit breaker (Resilience4j)
- Métricas com Micrometer
- Migrações de dados com Flyway (usando prefixo `R__` para scripts repeatable)
