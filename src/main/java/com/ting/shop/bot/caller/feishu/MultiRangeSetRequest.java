package com.ting.shop.bot.caller.feishu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname MultiRangeSetRequest
 * @Description 多个范围写入数据请求
 * @Date 2025/1/1 10:06
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiRangeSetRequest {

    private List<ValueRange> valueRanges;

}
