package br.com.setebit.vendasml.client;

import br.com.setebit.vendasml.dto.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Client Feign para API de Categorias do Mercado Livre
 * Nota: Endpoints de categorias são públicos e não requerem autenticação
 */
@FeignClient(
    name = "mercadoLivreCategoryClient",
    url = "${mercadolivre.base-url}"
)
public interface MercadoLivreCategoryClient {
    
    /**
     * Lista todas as categorias principais de um site
     * @param siteId ID do site (ex: MLB, MLA, MLM)
     * @return Lista de categorias principais
     */
    @GetMapping("/sites/{siteId}/categories")
    List<CategoryResponse> getCategories(@PathVariable("siteId") String siteId);
    
    /**
     * Obtém detalhes de uma categoria específica, incluindo subcategorias
     * @param categoryId ID da categoria (ex: MLB5672)
     * @return Detalhes da categoria com children_categories
     */
    @GetMapping("/categories/{categoryId}")
    CategoryResponse getCategory(@PathVariable("categoryId") String categoryId);
}

