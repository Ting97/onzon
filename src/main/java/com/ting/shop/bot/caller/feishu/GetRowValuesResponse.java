package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname GetRowValues
 * @Description TODO
 * @Date 2025/1/1 16:31
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetRowValuesResponse {
    private int code;
    private String msg;
    private GetRowValuesData data;
}
