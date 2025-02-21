package com.ting.shop.bot.task.dto.promotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname PromotionStatisticsResponse
 * @Description TODO
 * @Date 2025/1/4 11:53
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionStatisticsResponse {
    @JsonProperty("UUID")
    private String uuId;
}
