package com.ting.shop.bot.task.dto.analytics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname AnalyticsResponse
 * @Description 分析结果响应
 * @Date 2024/12/22 22:11
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticsResponse {

    private AnalyticsResult result;
}


