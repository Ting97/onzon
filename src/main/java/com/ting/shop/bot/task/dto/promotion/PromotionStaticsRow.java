package com.ting.shop.bot.task.dto.promotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PromotionStaticsRow
 * @Description TODO
 * @Date 2025/1/10 21:08
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionStaticsRow {
    private String sku;
    private String views = "0";
    private String orders = "0";
    private String ordersMoney = "0";
    private String moneySpent = "0";
    private String clicks = "0";
    private String toCart = "0";
    private String to_cart = "0";
    private String ctr;
}
