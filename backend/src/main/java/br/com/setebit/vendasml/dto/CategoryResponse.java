package br.com.setebit.vendasml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class CategoryResponse {
    private String id;
    private String name;
    
    @JsonProperty("children_categories")
    private List<CategoryResponse> childrenCategories;
    
    @JsonProperty("settings")
    private CategorySettings settings;
    
    @Data
    public static class CategorySettings {
        @JsonProperty("adult_content")
        private Boolean adultContent;
        
        @JsonProperty("buying_allowed")
        private Boolean buyingAllowed;
        
        @JsonProperty("buying_modes")
        private List<String> buyingModes;
        
        @JsonProperty("catalog_domain")
        private String catalogDomain;
        
        @JsonProperty("coverage_areas")
        private String coverageAreas;
        
        @JsonProperty("currencies")
        private List<String> currencies;
        
        @JsonProperty("fragile")
        private Boolean fragile;
        
        @JsonProperty("immediate_payment")
        private String immediatePayment;
        
        @JsonProperty("item_conditions")
        private List<String> itemConditions;
        
        @JsonProperty("items_reviews_allowed")
        private Boolean itemsReviewsAllowed;
        
        @JsonProperty("listing_allowed")
        private Boolean listingAllowed;
        
        @JsonProperty("max_description_length")
        private Integer maxDescriptionLength;
        
        @JsonProperty("max_pictures_per_item")
        private Integer maxPicturesPerItem;
        
        @JsonProperty("max_sub_title_length")
        private Integer maxSubTitleLength;
        
        @JsonProperty("max_title_length")
        private Integer maxTitleLength;
        
        @JsonProperty("maximum_price")
        private Integer maximumPrice;
        
        @JsonProperty("minimum_price")
        private Integer minimumPrice;
        
        @JsonProperty("mirror_category")
        private String mirrorCategory;
        
        @JsonProperty("mirror_master_category")
        private String mirrorMasterCategory;
        
        @JsonProperty("price")
        private String price;
        
        @JsonProperty("reservation_allowed")
        private String reservationAllowed;
        
        @JsonProperty("restrictions")
        private List<String> restrictions;
        
        @JsonProperty("rounded_address")
        private Boolean roundedAddress;
        
        @JsonProperty("seller_contact")
        private String sellerContact;
        
        @JsonProperty("shipping_modes")
        private List<String> shippingModes;
        
        @JsonProperty("shipping_options")
        private List<String> shippingOptions;
        
        @JsonProperty("shipping_profile")
        private String shippingProfile;
        
        @JsonProperty("show_contact_information")
        private Boolean showContactInformation;
        
        @JsonProperty("simple_shipping")
        private String simpleShipping;
        
        @JsonProperty("stock")
        private String stock;
        
        @JsonProperty("sub_vertical")
        private String subVertical;
        
        @JsonProperty("subscribable")
        private Boolean subscribable;
        
        @JsonProperty("tags")
        private List<String> tags;
        
        @JsonProperty("vertical")
        private String vertical;
        
        @JsonProperty("vip_subdomain")
        private String vipSubdomain;
        
        @JsonProperty("buyer_protection_programs")
        private List<String> buyerProtectionPrograms;
    }
}

