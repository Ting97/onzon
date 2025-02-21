package com.ting.shop.bot.caller.feishu;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname FeiShuCopySheet
 * @Description TODO
 * @Date 2024/12/28 22:42
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopySheet {

    private SheetSource source;
    private SheetDestination destination;
}
