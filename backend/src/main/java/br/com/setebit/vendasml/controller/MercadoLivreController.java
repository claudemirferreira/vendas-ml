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
    
    @Operation(
        summary = "Listar categorias de um site",
        description = "Lista todas as categorias principais disponíveis em um site específico do Mercado Livre. Não requer autenticação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorias obtida com sucesso",
                content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Site ID inválido")
    })
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @Parameter(description = "ID do site (ex: MLB para Brasil, MLA para Argentina)", required = true)
            @RequestParam String siteId) {
        List<CategoryResponse> categories = mercadoLivreService.getCategories(siteId);
        return ResponseEntity.ok(categories);
    }
    
    @Operation(
        summary = "Obter detalhes de uma categoria",
        description = "Obtém os detalhes de uma categoria específica, incluindo subcategorias (children_categories). Não requer autenticação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalhes da categoria obtidos com sucesso",
                content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/categorias/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(description = "ID da categoria (ex: MLB5672)", required = true)
            @PathVariable String categoryId) {
        CategoryResponse category = mercadoLivreService.getCategory(categoryId);
        return ResponseEntity.ok(category);
    }
}

