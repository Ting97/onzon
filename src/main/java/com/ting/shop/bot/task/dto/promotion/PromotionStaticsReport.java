package com.ting.shop.bot.task.dto.promotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname PromotionStaticsReport
 * @Description TODO
 * @Date 2025/1/9 21:23
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionStaticsReport {
    private List<PromotionStaticsRow> rows;

    private PromotionStaticsRow totals;
}
