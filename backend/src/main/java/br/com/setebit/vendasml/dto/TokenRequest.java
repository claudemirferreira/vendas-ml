package br.com.setebit.vendasml.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {
    @NotBlank(message = "Código de autorização é obrigatório")
    private String code;
}

