package com.ting.shop.bot.task.dto;

import lombok.*;

/**
 * @Classname Data
 * @Description  商品分析
 * @Date 2024/12/23 22:19
 * @Author by chenlt
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GoodAnalysis {

    private String sku;
    private String productId;
    private String offerId;
    private Double myPrice;
    private Double buyPrice;

    /**
     * 店铺名
     */
    private String name;

    /**
     * 展示总数
     */
    private Integer hitsView;
    /**
     * 查看过商品卡片的唯一身份访问者
     */
    private Integer sessionViewPdp;
    /**
     * 订购金额
     */
    private Integer revenue;
    /**
     * 订购数量
     */
    private Integer orderedUnits;
    /**
     * 加入购物车次数
     */
    private Integer hitsTocart;
    /**
     * 品类位置
     */
    private Integer positionCategory;
    /**
     * 商品已取消
     */
    private Integer cancellations;
    /**
     * 退货次数
     */
    private Integer returns;


    private double activityOrders;
    private double activityOrdersMoney;
    private double activityMoneySpent;
    private double activityViews;
    private double activityClicks;
    private double activityToCart;
    private double activityCtr;

    private double searchOrders;
    private double searchOrdersMoney;
    private double searchMoneySpent;
    private double searchViews;
    private double searchClicks;
    private double searchToCart;
    private double searchCtr;


}
