package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname RichMsgContent
 * @Description TODO
 * @Date 2025/1/20 21:43
 * @Author by chenlt
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RichMsgContent {
    private String tag;
    private String text;
    @JsonProperty("image_key")
    private String imageKey;
}
