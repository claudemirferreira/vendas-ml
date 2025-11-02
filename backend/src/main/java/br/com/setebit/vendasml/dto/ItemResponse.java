package br.com.setebit.vendasml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemResponse {
    private String id;
    private String title;
    private Double price;
    
    @JsonProperty("available_quantity")
    private Integer availableQuantity;
    
    private String status;
    private String permalink;
}

