package br.com.setebit.vendasml.controller;

import br.com.setebit.vendasml.dto.ItemRequest;
import br.com.setebit.vendasml.dto.ItemResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/produto")
@RequiredArgsConstructor
@Tag(name = "Produto", description = "API para integração com Mercado Livre - Autenticação OAuth e gerenciamento de produtos")
public class ProdutoController {

    private final MercadoLivreService mercadoLivreService;

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
