package com.ting.shop.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Classname YmalConfig
 * @Description 配置读取
 * @Date 2024/12/22 15:19
 * @Author by chenlt
 */


@Configuration
@ConfigurationProperties(prefix = "bot-config")
@Data
public class Config {
    private List<ConfigItem> configItemList;

}
