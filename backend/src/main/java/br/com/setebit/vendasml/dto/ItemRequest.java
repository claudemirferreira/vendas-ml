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

