package com.ting.shop.bot.task.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname GoodsWarehouse
 * @Description 商品货物信息
 * @Date 2024/12/26 11:35
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Warehouse {

    @JsonProperty("product_id")
    private Integer productId;

    private Integer sku;
}
