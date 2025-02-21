package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname ValueRange
 * @Description TODO
 * @Date 2025/1/1 10:07
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueRange {

    private String range;

    private List<List<Object>> values;
}
