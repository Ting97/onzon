package com.ting.shop.bot.config;

import lombok.Data;
import lombok.ToString;

/**
 * @Classname Config
 * @Description 配置类
 * @Date 2024/12/22 15:22
 * @Author by chenlt
 */
@Data
@ToString
public class ConfigItem {
    private String name;
    private String clientIdC;
    private String clientSecret;
    private String apiKey;
    private String clientIdP;
    private String feiShuWebhook;
    private String feiShuAppId;
    private String feiShuAppSecret;
    private String feiShuExcelToken;
    private String feiShuHourExcelToken;

}
