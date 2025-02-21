package com.ting.shop.bot.task.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname ProductListItem
 * @Description TODO
 * @Date 2024/12/24 22:22
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListItem {
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("offer_id")
    private String offerId;
}
