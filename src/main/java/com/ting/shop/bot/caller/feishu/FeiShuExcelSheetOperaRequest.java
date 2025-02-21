package com.ting.shop.bot.caller.feishu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname FeishuExcelSheetOperaRequest
 * @Description TODO
 * @Date 2024/12/28 22:41
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuExcelSheetOperaRequest {
    List<CopySheetRequest> requests;
}
