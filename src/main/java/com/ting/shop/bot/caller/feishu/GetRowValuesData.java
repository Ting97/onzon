package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname GetRowValuesData
 * @Description TODO
 * @Date 2025/1/1 16:32
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetRowValuesData {
    private String revision;
    private String spreadsheetToken;
    private ValueRange valueRange;
}
