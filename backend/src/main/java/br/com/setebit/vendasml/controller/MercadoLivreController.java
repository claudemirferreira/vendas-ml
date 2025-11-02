package br.com.setebit.vendasml.controller;

import br.com.setebit.vendasml.dto.*;
import br.com.setebit.vendasml.service.MercadoLivreService;

import java.util.List;
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

}

