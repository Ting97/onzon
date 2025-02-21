package com.ting.shop.bot.task.dto.analytics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

/**
 * @Classname AnalyticsData
 * @Description 分析数据
 * @Date 2024/12/22 22:18
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticsData {

    private List<Dimension> dimensions;

    private List<Double> metrics;

}
