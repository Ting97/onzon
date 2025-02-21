package com.ting.shop.bot.task.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname AnalyticsEnums
 * @Description 分析枚举
 * @Date 2024/12/22 22:33
 * @Author by chenlt
 */
@Getter
@AllArgsConstructor
public enum AnalyticsEnums {
    HITS_VIEW("hits_view", "总访问量"),
    SESSION_VIEW_PDP("session_view_pdp", "产品详情页访问量"),
    REVENUE("revenue", "订购金额"),
    ORDERED_UNITS("ordered_units", "订购数量"),
    HITS_TOCART("hits_tocart", "添加购物车，总计"),
    POSITION_CATEGORY("position_category", "搜索和目录中的位置"),
    CANCELLATIONS("cancellations", "取消订单数"),
    RETURNS("returns", "退货数"),

    ;

    private final String id;
    private final String name;

    public static List<String> getCond() {
        List<String> list = new ArrayList<>();
        for (AnalyticsEnums enums : values()) {
            list.add(enums.getId());
        }
        return list;
    }
}
