package br.com.setebit.vendasml.controller;

import br.com.setebit.vendasml.dto.CategoryResponse;
import br.com.setebit.vendasml.service.MercadoLivreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categoria")
@RequiredArgsConstructor
@Tag(name = "Mercado Livre", description = "API para integração com Mercado Livre - Autenticação OAuth e gerenciamento de produtos")
public class CategoriaController {

    private final MercadoLivreService mercadoLivreService;

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

