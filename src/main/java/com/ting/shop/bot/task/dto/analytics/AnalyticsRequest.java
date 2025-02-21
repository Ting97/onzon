package com.ting.shop.bot.task.dto.analytics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * @Classname AnalyticsRequest
 * @Description 分析请求
 * @Date 2024/12/22 17:49
 * @Author by chenlt
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticsRequest {

    @JsonProperty("date_from")
    private String dateFrom;
    @JsonProperty("date_to")
    private String dateTo;
    private List<String> metrics;
    private List<String> dimension;
    private List<String> filters;
    private Sort sort;
    private int limit;
    private int offset;

}
