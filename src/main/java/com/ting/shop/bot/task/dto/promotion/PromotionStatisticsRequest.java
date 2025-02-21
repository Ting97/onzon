package com.ting.shop.bot.task.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname PromotionStatisticsRequest
 * @Description TODO
 * @Date 2025/1/4 11:52
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionStatisticsRequest {

    private List<String> campaigns;
    private String from;
    private String to;
    private String groupBy = "NO_GROUP_BY";
}
