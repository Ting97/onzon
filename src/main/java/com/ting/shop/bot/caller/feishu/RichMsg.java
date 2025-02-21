package com.ting.shop.bot.caller.feishu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname RichMsg
 * @Description TODO
 * @Date 2025/1/20 21:41
 * @Author by chenlt
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RichMsg {
    private String title;
    private List<RichMsgContent> content;

}
