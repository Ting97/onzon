package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname FeiShuRequest
 * @Description 飞书机器人
 * @Date 2024/12/28 15:36
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuBotMsgRequest {
    @JsonProperty("msg_type")
    private String msgType;
    private TextContent content;

}
