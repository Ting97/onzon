package com.ting.shop.bot.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname ProductListFilter
 * @Description 商品里列表筛选条件
 * @Date 2024/12/24 22:15
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListFilter {
    @JsonProperty("offer_id")
    private List<String> offerId;
    @JsonProperty("product_id")
    private List<String> productId;


    private String visibility;
}
