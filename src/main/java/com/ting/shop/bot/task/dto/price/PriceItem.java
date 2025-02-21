package com.ting.shop.bot.task.dto.price;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PriceItem
 * @Description TODO
 * @Date 2024/12/28 11:36
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceItem {
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("offer_id")
    private String offerId;

    private PriceInfo price;
}
