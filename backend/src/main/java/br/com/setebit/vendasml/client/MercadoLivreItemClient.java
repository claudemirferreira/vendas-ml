package br.com.setebit.vendasml.client;

import br.com.setebit.vendasml.config.FeignConfig;
import br.com.setebit.vendasml.dto.ItemRequest;
import br.com.setebit.vendasml.dto.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "mercadoLivreItemClient", 
    url = "${mercadolivre.base-url}",
    configuration = FeignConfig.class
)
public interface MercadoLivreItemClient {
    
    @PostMapping("/items")
    ItemResponse createItem(
        @RequestHeader("Authorization") String authorization,
        @RequestBody ItemRequest request
    );
    
    @GetMapping("/items/{itemId}")
    ItemResponse getItem(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("itemId") String itemId
    );
    
    @PutMapping("/items/{itemId}")
    ItemResponse updateItem(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("itemId") String itemId,
        @RequestBody ItemRequest request
    );
    
    @DeleteMapping("/items/{itemId}")
    void deleteItem(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("itemId") String itemId
    );
}

