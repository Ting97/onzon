package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname TestContent
 * @Description TODO
 * @Date 2024/12/28 15:37
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextContent {
    private String text;
    @JsonProperty("image_key")
    private String imageKey;
}
