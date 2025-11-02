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

