package com.ting.shop.bot.task.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ting.shop.bot.task.dto.ListFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname ProductRequest
 * @Description 商品列表请求
 * @Date 2024/12/24 22:13
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListRequest {

    private ListFilter filter;
    @JsonProperty("last_id")
    private String lastId;

    private Integer limit;

}
