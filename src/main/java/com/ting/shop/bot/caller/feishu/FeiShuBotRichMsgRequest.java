package com.ting.shop.bot.caller.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
public class FeiShuBotRichMsgRequest {
    @JsonProperty("msg_type")
    private String msgType;

    private Map<String, Map<String, RichMsg>> content;

    public FeiShuBotRichMsgRequest buildContent(String title, String imageKey) {
        this.content = Map.of("post", Map.of("zh_cn",
                RichMsg.builder()
                        .title(title)
                        .content(List.of(RichMsgContent.builder().tag("img").imageKey(imageKey).build()))
                        .build()));
        return this;
    }

}
