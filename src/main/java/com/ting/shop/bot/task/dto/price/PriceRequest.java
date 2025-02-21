package com.ting.shop.bot.task.dto.price;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ting.shop.bot.task.dto.ListFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname PriceRequest
 * @Description 价格请求
 * @Date 2024/12/28 11:07
 * @Author by chenlt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceRequest {

    private ListFilter filter;

    @JsonProperty("last_id")
    private String lastId;

    private Integer limit;
}
