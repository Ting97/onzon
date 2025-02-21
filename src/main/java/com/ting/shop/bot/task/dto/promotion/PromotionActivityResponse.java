package com.ting.shop.bot.task.dto.promotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname PromotionActivity
 * @Description TODO
 * @Date 2025/1/4 11:44
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionActivityResponse {
    private List<PromotionActivity> list;
}
