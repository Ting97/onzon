package com.ting.shop.bot.task.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

/**
 * @Classname ProductListResult
 * @Description TODO
 * @Date 2024/12/24 22:20
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListResult {

    private List<ProductListItem> items;
    private int total;
    @JsonProperty("last_id")
    private String lastId;
}
