package com.ting.shop.bot.caller.feishu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname ValuesAppendRequest
 * @Description TODO
 * @Date 2025/1/22 20:58
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValuesAppendRequest {
    private ValueRange valueRange;
}
