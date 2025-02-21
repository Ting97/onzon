package com.ting.shop.bot.task.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname AnalyticsResult
 * @Description 分析结果
 * @Date 2024/12/28 11:46
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResult {
    private List<AnalyticsData> data;
    private List<Double> totals;
}
