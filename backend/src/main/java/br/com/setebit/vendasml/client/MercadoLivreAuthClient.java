package br.com.setebit.vendasml.client;

import br.com.setebit.vendasml.config.FeignConfig;
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

