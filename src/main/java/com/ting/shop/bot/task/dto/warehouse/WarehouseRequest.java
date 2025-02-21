package com.ting.shop.bot.task.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname GoodsWarehouse
 * @Description 商品货物
 * @Date 2024/12/26 11:32
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseRequest {
    List<String> sku;
}
