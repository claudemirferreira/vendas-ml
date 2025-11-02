package br.com.setebit.vendasml.service;

import br.com.setebit.vendasml.client.MercadoLivreAuthClient;
import br.com.setebit.vendasml.client.MercadoLivreCategoryClient;
import br.com.setebit.vendasml.client.MercadoLivreItemClient;
import br.com.setebit.vendasml.dto.CategoryResponse;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoLivreService {
    
    private final MercadoLivreAuthClient authClient;
    private final MercadoLivreItemClient itemClient;
    private final MercadoLivreCategoryClient categoryClient;
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
    
    /**
     * Lista todas as categorias principais de um site
     * @param siteId ID do site (ex: MLB para Brasil, MLA para Argentina)
     * @return Lista de categorias principais
     */
    public List<CategoryResponse> getCategories(String siteId) {
        log.info("Listando categorias do site: {}", siteId);
        try {
            return categoryClient.getCategories(siteId);
        } catch (Exception e) {
            log.error("Erro ao listar categorias: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Erro ao listar categorias: " + e.getMessage()
            );
        }
    }
    
    /**
     * Obtém detalhes de uma categoria específica, incluindo subcategorias
     * @param categoryId ID da categoria (ex: MLB5672)
     * @return Detalhes da categoria com subcategorias
     */
    public CategoryResponse getCategory(String categoryId) {
        log.info("Buscando categoria: {}", categoryId);
        try {
            return categoryClient.getCategory(categoryId);
        } catch (Exception e) {
            log.error("Erro ao buscar categoria: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Erro ao buscar categoria: " + e.getMessage()
            );
        }
    }
}

