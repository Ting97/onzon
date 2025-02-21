package com.ting.shop.bot.task.dto.analytics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @Classname Sort
 * @Description TODO
 * @Date 2024/12/22 22:16
 * @Author by chenlt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sort {
    private String key;
    private String sort;
}
