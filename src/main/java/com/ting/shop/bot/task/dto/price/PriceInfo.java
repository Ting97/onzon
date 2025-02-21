package com.ting.shop.bot.task.dto.price;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PriceInfo
 * @Description 价格
 * @Date 2024/12/28 11:17
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInfo {

    /**
     * 买家价格
     */
    @JsonProperty("marketing_price")
    private Double marketPrice;

    /**
     * 市场卖家价格
     */
    @JsonProperty("marketing_seller_price")
    private Double marketSellerPrice;
}
