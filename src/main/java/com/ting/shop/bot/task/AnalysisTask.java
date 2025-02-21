package com.ting.shop.bot.task;

/**
 * @Classname Task
 * @Description TODO
 * @Date 2024/12/22 15:59
 * @Author by chenlt
 */

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.service.im.v1.model.CreateImageReq;
import com.lark.oapi.service.im.v1.model.CreateImageReqBody;
import com.lark.oapi.service.im.v1.model.CreateImageResp;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetReq;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetResp;
import com.lark.oapi.service.sheets.v3.model.Sheet;
import com.ting.shop.bot.caller.FeiShuExcelCaller;
import com.ting.shop.bot.caller.feishu.GetRowValuesResponse;
import com.ting.shop.bot.caller.feishu.ValueRange;
import com.ting.shop.bot.common.Constants;
import com.ting.shop.bot.config.Config;
import com.ting.shop.bot.config.ConfigItem;
import com.ting.shop.bot.image.ImageService;
import com.ting.shop.bot.task.dto.GoodAnalysis;
import com.ting.shop.bot.task.dto.ListFilter;
import com.ting.shop.bot.task.dto.analytics.AnalyticsData;
import com.ting.shop.bot.task.dto.analytics.AnalyticsEnums;
import com.ting.shop.bot.task.dto.analytics.AnalyticsRequest;
import com.ting.shop.bot.task.dto.analytics.AnalyticsResponse;
import com.ting.shop.bot.task.dto.price.PriceItem;
import com.ting.shop.bot.task.dto.price.PriceRequest;
import com.ting.shop.bot.task.dto.price.PriceResponse;
import com.ting.shop.bot.task.dto.product.ProductInfoItem;
import com.ting.shop.bot.task.dto.product.ProductInfoRequest;
import com.ting.shop.bot.task.dto.product.ProductInfoResponse;
import com.ting.shop.bot.task.dto.promotion.*;
import com.ting.shop.bot.task.dto.promotion.token.TokenRequest;
import com.ting.shop.bot.task.dto.promotion.token.TokenResponse;
import com.ting.shop.bot.task.dto.warehouse.Warehouse;
import com.ting.shop.bot.task.dto.warehouse.WarehouseRequest;
import com.ting.shop.bot.task.dto.warehouse.WarehouseResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@EnableScheduling
public class AnalysisTask {
    public static final ThreadLocal<String> TODAY = new ThreadLocal<>();
    public static final ThreadLocal<String> YESTERDAY = new ThreadLocal<>();
    public static final ThreadLocal<LocalDateTime> TIME = new ThreadLocal<>();
    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .callTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build();
    private static final Logger LOGGER = Logger.getLogger(AnalysisTask.class.getName());
    @Autowired
    private Config config;
    @Autowired
    private FeiShuExcelCaller feiShuExcelCaller;
    @Autowired
    private ImageService imageService;


    @Scheduled(cron = "0 0 */1 * * *")
    public void hourAnalysis() throws Exception {

        initDateTime();
        int times = 0;
        for (int i = 0; i < config.getConfigItemList().size(); i++) {
            ConfigItem configItem = config.getConfigItemList().get(i);
            try {
                LOGGER.info(configItem.getName() + ",开始执行" + times + "次");

                Client client = Client.newBuilder(configItem.getFeiShuAppId(), configItem.getFeiShuAppSecret())
                        .requestTimeout(600, TimeUnit.SECONDS)
                        .build();
                List<GoodAnalysis> goodAnalysisList = new ArrayList<>();

                // 分析
                LocalDateTime now = LocalDateTime.now();
                if (now.getHour() > 5) {
                    analysis(TODAY.get(), configItem, goodAnalysisList);
                } else {
                    analysis(YESTERDAY.get(), configItem, goodAnalysisList);
                }

                LOGGER.info("分析结果 " + goodAnalysisList);
                if (CollectionUtils.isEmpty(goodAnalysisList)) {
                    continue;
                }

                Map<String, GoodAnalysis> skuMap = goodAnalysisList.stream().collect(Collectors.toMap(GoodAnalysis::getSku, Function.identity(), (a, b) -> a));
                LOGGER.info("sku " + skuMap);
                // 商品详情
                productInfo(configItem, skuMap);
                Map<String, GoodAnalysis> productMap = skuMap.values().stream().collect(Collectors.toMap(GoodAnalysis::getProductId, Function.identity(), (a, b) -> a));
                // 价格
                price(configItem, productMap);
                LOGGER.info("product " + productMap);
                List<GoodAnalysis> goodAnalyses = productMap.values().stream().sorted((a, b) -> {
                    if (b.getOrderedUnits().compareTo(a.getOrderedUnits()) == 0) {
                        return b.getHitsView().compareTo(a.getHitsView());
                    }
                    return b.getOrderedUnits().compareTo(a.getOrderedUnits());
                }).peek(g -> g.setName(configItem.getName())).collect(Collectors.toList());
                List<List<Object>> data = new ArrayList<>();
                // 飞书文档统计
                docHour2FeiShu(client, configItem, goodAnalyses, data);
                // 推送消息至飞书
                if (!StringUtil.isNullOrEmpty(configItem.getFeiShuWebhook())) {
                    feiShuBotMsg(client, configItem, data);
                }
            } catch (Exception e) {
                LOGGER.severe("执行失败 " + e.getMessage());
                if (times < 3) {
                    times++;
                    i--;
                    Thread.sleep(61 * 1000);
                } else {
                    times = 0;
                }
            }
        }

    }

    @Scheduled(cron = "0 30 9 * * *")
    public void dayAnalysis() throws Exception {

        initDateTime();
        int times = 0;
        for (int i = 0; i < config.getConfigItemList().size(); i++) {
            ConfigItem configItem = config.getConfigItemList().get(i);

            try {

                LOGGER.info(configItem.getName() + ",开始执行");
                Client client = Client.newBuilder(configItem.getFeiShuAppId(), configItem.getFeiShuAppSecret())
                        .requestTimeout(600, TimeUnit.SECONDS)
                        .build();
                List<GoodAnalysis> goodAnalysisList = new ArrayList<>();
                // 昨日分析
                try {
                    analysis(YESTERDAY.get(), configItem, goodAnalysisList);
                } catch (Exception e) {
                    LOGGER.severe("分析失败 " + e.getMessage());
                    continue;
                }
                LOGGER.info("分析结果 " + goodAnalysisList);
                if (CollectionUtils.isEmpty(goodAnalysisList)) {
                    continue;
                }
                Map<String, GoodAnalysis> skuMap = goodAnalysisList.stream().collect(Collectors.toMap(GoodAnalysis::getSku, Function.identity(), (a, b) -> a));
                LOGGER.info("sku " + skuMap);
                // 商品详情
                productInfo(configItem, skuMap);
                Map<String, GoodAnalysis> productMap = skuMap.values().stream().collect(Collectors.toMap(GoodAnalysis::getProductId, Function.identity(), (a, b) -> a));
                // 价格
                price(configItem, productMap);
                LOGGER.info("product " + productMap);
                List<GoodAnalysis> goodAnalyses = productMap.values().stream().sorted((a, b) -> {
                    if (b.getOrderedUnits().compareTo(a.getOrderedUnits()) == 0) {
                        return b.getHitsView().compareTo(a.getHitsView());
                    }
                    return b.getOrderedUnits().compareTo(a.getOrderedUnits());
                }).peek(g -> g.setName(configItem.getName())).collect(Collectors.toList());
                // 最后一小时需要处理推广分析数据
                promotionAnalysis(configItem, goodAnalyses);

                // 飞书文档统计
                docDay2FeiShu(client, configItem, goodAnalyses);
            } catch (Exception e) {
                LOGGER.severe("执行失败 " + e.getMessage());
                if (times < 3) {
                    times++;
                    i--;
                    Thread.sleep(61 * 1000);
                } else {
                    times = 0;
                }
            }
        }

    }


    private void initDateTime() {
        LocalDate currentDate = LocalDate.now();
        TODAY.set(currentDate.toString());
        currentDate = currentDate.minusDays(1);
        YESTERDAY.set(currentDate.toString());
        LocalDateTime now = LocalDateTime.now();
        TIME.set(now);
    }

    private void docHour2FeiShu(Client client, ConfigItem configItem, List<GoodAnalysis> goodAnalyses, List<List<Object>> data) throws Exception {
        String feiShuHourExcelToken = StringUtil.isNullOrEmpty(configItem.getFeiShuHourExcelToken()) ? configItem.getFeiShuExcelToken() : configItem.getFeiShuHourExcelToken();
        LOGGER.info("记录token" + feiShuHourExcelToken);
        // 获取飞书文档的sheet信息
        Map<String, Sheet> sheetMap = getNameSheetMap(client, feiShuHourExcelToken);
        LOGGER.info(OBJECTMAPPER.writeValueAsString(sheetMap));
        // 如果未创建创建文档Sheet
        for (GoodAnalysis goodAnalysis : goodAnalyses) {
            String offerId = goodAnalysis.getOfferId();
            if (!sheetMap.containsKey(offerId + Constants.SHEET_TITLE_HOUR)) {
                createHourSheet2Map(client, feiShuHourExcelToken, offerId, sheetMap);
            }
        }

        // 获取飞书文档的sheet信息
        sheetMap = getNameSheetMap(client, feiShuHourExcelToken);
        LOGGER.info(OBJECTMAPPER.writeValueAsString(sheetMap));

        writeHour2Sheet(client, feiShuHourExcelToken, sheetMap, goodAnalyses, data);
    }

    private void docDay2FeiShu(Client client, ConfigItem configItem, List<GoodAnalysis> goodAnalyses) throws Exception {
        String feiShuExcelToken = configItem.getFeiShuExcelToken();
        // 获取飞书文档的sheet信息
        Map<String, Sheet> sheetMap = getNameSheetMap(client, feiShuExcelToken);
        LOGGER.info(OBJECTMAPPER.writeValueAsString(sheetMap));
        // 如果未创建创建文档Sheet
        for (GoodAnalysis goodAnalysis : goodAnalyses) {
            String offerId = goodAnalysis.getOfferId();
            if (!sheetMap.containsKey(offerId + Constants.SHEET_TITLE_DAY)) {
                createDaySheet2Map(client, feiShuExcelToken, offerId, sheetMap);
            }
        }

        // 获取飞书文档的sheet信息
        sheetMap = getNameSheetMap(client, feiShuExcelToken);

        writeDay2Sheet(client, configItem, sheetMap, goodAnalyses);
    }

    private void writeHour2Sheet(Client client, String excelToken, Map<String, Sheet> sheetMap, List<GoodAnalysis> goodAnalyses, List<List<Object>> data) throws Exception {
        String sheetId = sheetMap.get(Constants.HOUR_TEMPLATE).getSheetId();
        data.add(getRowDate(client, excelToken, sheetId + "!A1:M1"));
        for (GoodAnalysis goodAnalysis : goodAnalyses) {
            write2Hour(client, excelToken, sheetMap, goodAnalysis, data);
        }
    }

    private void writeDay2Sheet(Client client, ConfigItem configItem, Map<String, Sheet> sheetMap, List<GoodAnalysis> goodAnalyses) throws Exception {
        for (GoodAnalysis goodAnalysis : goodAnalyses) {
            write2Day(client, configItem, sheetMap, goodAnalysis);
        }
    }

    private void write2Hour(Client client, String excelToken, Map<String, Sheet> sheetMap, GoodAnalysis goodAnalysis, List<List<Object>> data) throws Exception {
        String offerId = goodAnalysis.getOfferId();
        // 小时
        Sheet sheet = sheetMap.get(offerId + Constants.SHEET_TITLE_HOUR);
        String sheetId = sheet.getSheetId();
        String range = sheetId + "!A2:A1000";
        ValueRange valueRange = ValueRange.builder().range(range).values(List.of(List.of(goodAnalysis.getOfferId()))).build();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        int row = feiShuExcelCaller.valuesAppend(client, excelToken, valueRange);
        if (row == 0) {
            return;
        }
        Integer orderedUnits = goodAnalysis.getOrderedUnits();
        Integer revenue = goodAnalysis.getRevenue();
        Map<String, List<List<Object>>> valueMap = Map.of(
                sheetId + "!A" + row + ":C" + row, List.of(List.of(goodAnalysis.getOfferId(), goodAnalysis.getName(), TIME.get().format(formatter))),
                sheetId + "!D" + row + ":D" + row, List.of(List.of(goodAnalysis.getHitsView())),
                sheetId + "!H" + row + ":M" + row, List.of(List.of(goodAnalysis.getPositionCategory(), orderedUnits, revenue, orderedUnits == 0 ? 0 : revenue / orderedUnits, goodAnalysis.getSessionViewPdp(), goodAnalysis.getHitsTocart())));
        feiShuExcelCaller.setMultiValues(client, excelToken, valueMap);
        range = sheetId + "!A" + row + ":M" + row;
        data.add(getRowDate(client, excelToken, range));
    }

    private void write2Day(Client client, ConfigItem configItem, Map<String, Sheet> sheetMap, GoodAnalysis goodAnalysis) throws Exception {
        String offerId = goodAnalysis.getOfferId();
        Sheet sheet = sheetMap.get(offerId + Constants.SHEET_TITLE_DAY);
        String sheetId = sheet.getSheetId();
        String range = sheetId + "!A2:A1000";
        ValueRange valueRange = ValueRange.builder().range(range).values(List.of(List.of(goodAnalysis.getOfferId()))).build();
        int row = feiShuExcelCaller.valuesAppend(client, configItem.getFeiShuExcelToken(), valueRange);
        // 0表示服务报错，不进行记录
        if (row == 0) {
            return;
        }
        Integer orderedUnits = goodAnalysis.getOrderedUnits();
        Integer revenue = goodAnalysis.getRevenue();
        Map<String, List<List<Object>>> valueMap = Map.of(
                sheetId + "!A1:A1", List.of(List.of(goodAnalysis.getOfferId())),
                sheetId + "!A" + row + ":C" + row, List.of(List.of(goodAnalysis.getOfferId(), goodAnalysis.getName(), YESTERDAY.get())),
                sheetId + "!D" + row + ":D" + row, List.of(List.of(goodAnalysis.getHitsView())),
                sheetId + "!H" + row + ":K" + row, List.of(List.of(goodAnalysis.getPositionCategory(), orderedUnits, revenue, orderedUnits == 0 ? 0 : revenue / orderedUnits)),
                sheetId + "!T" + row + ":U" + row, List.of(List.of(goodAnalysis.getBuyPrice(), goodAnalysis.getMyPrice())),
                sheetId + "!Z" + row + ":AC" + row, List.of(List.of(goodAnalysis.getSessionViewPdp(), goodAnalysis.getHitsTocart(), goodAnalysis.getCancellations(), goodAnalysis.getReturns())),
                sheetId + "!AD" + row + ":AH" + row, List.of(List.of(goodAnalysis.getActivityViews(), goodAnalysis.getActivityClicks())),
                sheetId + "!AG" + row + ":AH" + row, List.of(List.of(goodAnalysis.getActivityOrders(), goodAnalysis.getActivityOrdersMoney())),
                sheetId + "!AJ" + row + ":AJ" + row, List.of(List.of(goodAnalysis.getActivityMoneySpent())),
                sheetId + "!AL" + row + ":AP" + row, List.of(List.of(goodAnalysis.getSearchOrdersMoney(), goodAnalysis.getSearchOrders(), goodAnalysis.getSearchMoneySpent(), goodAnalysis.getSearchViews(), goodAnalysis.getSearchClicks()))
        );
        feiShuExcelCaller.setMultiValues(client, configItem.getFeiShuExcelToken(), valueMap);
    }

    private void promotionAnalysis(ConfigItem configItem, List<GoodAnalysis> goodAnalysis) throws Exception {

        // 获取推广统计token
        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        TokenRequest tokenRequest = TokenRequest.builder()
                .clientId(configItem.getClientIdC())
                .clientSecret(configItem.getClientSecret())
                .grantType(Constants.GRANT_TYPE_CLIENT_CREDENTIALS)
                .build();
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(tokenRequest));

        Request request = new Request.Builder()
                .url(Constants.TOKEN_REQUEST)
                .method(Constants.POST, body)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();
        Response response = CLIENT.newCall(request).execute();
        String token;
        if (response.isSuccessful() && response.body() != null) {
            TokenResponse tokenResponse = OBJECTMAPPER.readValue(response.body().bytes(), TokenResponse.class);
            token = tokenResponse.getAccessToken();
        } else {
            LOGGER.warning("获取搜推token服务端错误: " + request + response);
            response.close();
            return;
        }
        response.close();

        // 获取推广活动列表
        request = new Request.Builder()
                .url(Constants.ACTIVITY_REQUEST)
                .method(Constants.GET, null)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .addHeader(Constants.AUTHORIZATION, Constants.BEARER + token)
                .build();

        response = CLIENT.newCall(request).execute();

        List<String> activityIdList;
        if (response.isSuccessful() && response.body() != null) {
            PromotionActivityResponse promotionActivityResponse = OBJECTMAPPER.readValue(response.body().bytes(), PromotionActivityResponse.class);
            activityIdList = promotionActivityResponse.getList().stream().map(PromotionActivity::getId).collect(Collectors.toList());
        } else {
            LOGGER.warning("获取推广活动服务端错误: " + request + response);
            response.close();
            return;
        }
        response.close();

        // 遍历推广活动，获取推广分析数据
        String activityUuId = getPromotionStatisticsUuid(activityIdList, token, Constants.PROMOTION_STATISTICS_REQUEST);

        boolean isContinue = true;
        int times = 0;
        while (StringUtil.notNullNorEmpty(activityUuId) && isContinue && times < 10) {
            times++;
            response = getResponse(token, activityUuId);
            if (response.isSuccessful() && response.body() != null) {
                Map<String, Object> map = ((Map<String, Object>) OBJECTMAPPER.readValue(response.body().bytes(), Map.class));
                List<PromotionStaticsRow> rows = new ArrayList<>();
                for (Object object : map.values()) {
                    PromotionStatics statics = OBJECTMAPPER.readValue(OBJECTMAPPER.writeValueAsString(object), PromotionStatics.class);
                    rows.addAll(statics.getReport().getRows());
                }
                Map<String, List<PromotionStaticsRow>> skuGoodMap = rows.stream().collect(Collectors.groupingBy(PromotionStaticsRow::getSku));
                Map<String, PromotionStaticsRow> skuMap = skuGoodMap.values().stream().map(e -> {
                    PromotionStaticsRow ans = new PromotionStaticsRow();
                    for (PromotionStaticsRow promotionStaticsRow : e) {
                        ans.setSku(promotionStaticsRow.getSku());

                        ans.setOrders(Double.toString(Double.sum(Double.parseDouble(ans.getOrders()), Double.parseDouble(promotionStaticsRow.getOrders().replace(',', '.')))));
                        ans.setMoneySpent(Double.toString(Double.sum(Double.parseDouble(ans.getMoneySpent()), Double.parseDouble(promotionStaticsRow.getMoneySpent().replace(',', '.')))));
                        ans.setViews(Double.toString(Double.sum(Double.parseDouble(ans.getViews()), Double.parseDouble(promotionStaticsRow.getViews().replace(',', '.')))));
                        ans.setOrdersMoney(Double.toString(Double.sum(Double.parseDouble(ans.getOrdersMoney()), Double.parseDouble(promotionStaticsRow.getOrdersMoney().replace(',', '.')))));
                        ans.setClicks(Double.toString(Double.sum(Double.parseDouble(ans.getClicks()), Double.parseDouble(promotionStaticsRow.getClicks().replace(',', '.')))));
                        ans.setToCart(Double.toString(Double.sum(Double.parseDouble(ans.getToCart()), Double.parseDouble(promotionStaticsRow.getToCart().replace(',', '.')))));
                    }
                    return ans;
                }).collect(Collectors.toMap(PromotionStaticsRow::getSku, Function.identity(), (key1, key2) -> key1));

                for (GoodAnalysis good : goodAnalysis) {
                    PromotionStaticsRow promotionStaticsRow = skuMap.get(good.getSku());
                    if (promotionStaticsRow != null) {
                        good.setActivityOrders(Double.parseDouble(promotionStaticsRow.getOrders().replace(',', '.')));
                        good.setActivityOrdersMoney(Double.parseDouble(promotionStaticsRow.getOrdersMoney().replace(',', '.')));
                        good.setActivityMoneySpent(Double.parseDouble(promotionStaticsRow.getMoneySpent().replace(',', '.')));
                        good.setActivityViews(Double.parseDouble(promotionStaticsRow.getViews().replace(',', '.')));
                        good.setActivityClicks(Double.parseDouble(promotionStaticsRow.getClicks().replace(',', '.')));
                        good.setActivityToCart(Double.parseDouble(promotionStaticsRow.getToCart().replace(',', '.')));
                    }
                }

                isContinue = false;
            } else {
                LOGGER.warning("activity服务端错误: " + request + response);
            }
        }
        response.close();
        // 遍历搜索推广，获取推广分析数据
        String searchUuId = getPromotionStatisticsUuid(activityIdList, token, Constants.SEARCH_STATISTICS_REQUEST);
        isContinue = true;
        times = 0;
        while (StringUtil.notNullNorEmpty(searchUuId) && isContinue && times < 10) {
            times++;
            response = getResponse(token, searchUuId);
            if (response.isSuccessful() && response.body() != null) {
                PromotionStatics promotionStatics = OBJECTMAPPER.readValue(response.body().bytes(), PromotionStatics.class);
                List<PromotionStaticsRow> rows = promotionStatics.getReport().getRows();
                Map<String, List<PromotionStaticsRow>> skuGoodMap = rows.stream().collect(Collectors.groupingBy(PromotionStaticsRow::getSku));
                Map<String, PromotionStaticsRow> skuMap = skuGoodMap.values().stream().map(e -> {
                    PromotionStaticsRow ans = new PromotionStaticsRow();
                    for (PromotionStaticsRow promotionStaticsRow : e) {
                        ans.setSku(promotionStaticsRow.getSku());
                        ans.setOrders(Double.toString(Double.sum(Double.parseDouble(ans.getOrders()), Double.parseDouble(promotionStaticsRow.getOrders().replace(',', '.')))));
                        ans.setMoneySpent(Double.toString(Double.sum(Double.parseDouble(ans.getMoneySpent()), Double.parseDouble(promotionStaticsRow.getMoneySpent().replace(',', '.')))));
                        ans.setViews(Double.toString(Double.sum(Double.parseDouble(ans.getViews()), Double.parseDouble(promotionStaticsRow.getViews().replace(',', '.')))));
                        ans.setOrdersMoney(Double.toString(Double.sum(Double.parseDouble(ans.getOrdersMoney()), Double.parseDouble(promotionStaticsRow.getOrdersMoney().replace(',', '.')))));
                        ans.setClicks(Double.toString(Double.sum(Double.parseDouble(ans.getClicks()), Double.parseDouble(promotionStaticsRow.getClicks().replace(',', '.')))));
                        ans.setTo_cart(Double.toString(Double.sum(Double.parseDouble(ans.getTo_cart()), Double.parseDouble(promotionStaticsRow.getTo_cart().replace(',', '.')))));
                    }
                    return ans;
                }).collect(Collectors.toMap(PromotionStaticsRow::getSku, Function.identity(), (key1, key2) -> key1));

                for (GoodAnalysis good : goodAnalysis) {
                    PromotionStaticsRow promotionStaticsRow = skuMap.get(good.getSku());
                    if (promotionStaticsRow != null) {
                        good.setSearchOrders(Double.parseDouble(promotionStaticsRow.getOrders().replace(',', '.')));
                        good.setSearchOrdersMoney(Double.parseDouble(promotionStaticsRow.getOrdersMoney().replace(',', '.')));
                        good.setSearchMoneySpent(Double.parseDouble(promotionStaticsRow.getMoneySpent().replace(',', '.')));
                        good.setSearchViews(Double.parseDouble(promotionStaticsRow.getViews().replace(',', '.')));
                        good.setSearchClicks(Double.parseDouble(promotionStaticsRow.getClicks().replace(',', '.')));
                        good.setSearchToCart(Double.parseDouble(promotionStaticsRow.getTo_cart().replace(',', '.')));
                    }
                }
                isContinue = false;
            } else {
                LOGGER.warning("search服务端错误: " + request + response);
            }
        }
        response.close();
    }

    private String getPromotionStatisticsUuid(List<String> activityIdList, String token, String url) throws Exception {
        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        PromotionStatisticsRequest statisticsRequest = PromotionStatisticsRequest.builder()
                .campaigns(activityIdList)
                .from(YESTERDAY.get() + Constants.DAY_TIME_START)
                .to(TODAY.get() + Constants.DAY_TIME_START)
                .build();
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(statisticsRequest));

        Request request = new Request.Builder()
                .url(url)
                .method(Constants.POST, body)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .addHeader(Constants.AUTHORIZATION, Constants.BEARER + token)
                .build();
        Response response = CLIENT.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            PromotionStatisticsResponse statisticsResponse = OBJECTMAPPER.readValue(response.body().bytes(), PromotionStatisticsResponse.class);
            response.close();
            return statisticsResponse.getUuId();
        } else {
            LOGGER.warning("获取报告uuId服务端错误: " + request + response);
            response.close();
            return null;
        }
    }

    private Response getResponse(String token, String uuid) throws Exception {

        Request request = new Request.Builder()
                .url(Constants.REPORT_DOWNLOAD_REQUEST + uuid)
                .method(Constants.GET, null)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .addHeader(Constants.AUTHORIZATION, Constants.BEARER + token)
                .build();
        Thread.sleep(60000);
        return CLIENT.newCall(request).execute();
    }

    private List<Object> getRowDate(Client client, String feiShuExcelToken, String range) throws Exception {

        RawResponse response = client.get(Constants.FEI_SHU_GET_RANGE_VALUE.replace(Constants.SPREADSHEET_TOKEN, feiShuExcelToken).replace(":range", range)
                + "?valueRenderOption=FormattedValue", null, AccessTokenType.Tenant);
        GetRowValuesResponse valuesResponse = OBJECTMAPPER.readValue(response.getBody(), GetRowValuesResponse.class);

        return valuesResponse.getData().getValueRange().getValues().get(0);
    }


    private Map<String, Sheet> getNameSheetMap(Client client, String excelToken) throws Exception {
        QuerySpreadsheetSheetReq req = QuerySpreadsheetSheetReq.newBuilder()
                .spreadsheetToken(excelToken)
                .build();

        QuerySpreadsheetSheetResp resp = client.sheets().spreadsheetSheet().query(req);

        if (!resp.success()) {
            LOGGER.warning("getSheetName服务端错误: " + resp.getCode() + ", " + resp.getMsg());
            throw new Exception("服务端错误: " + resp.getCode() + ", " + resp.getMsg());
        }
        return Stream.of(resp.getData().getSheets()).collect(Collectors.toMap(Sheet::getTitle, e -> e));
    }

    private void createHourSheet2Map(Client client, String excelToken, String
            offerId, Map<String, Sheet> sheetMap) throws Exception {
        Map<String, String> copyMap = Map.of(
                sheetMap.get(Constants.HOUR_TEMPLATE).getSheetId(), offerId + Constants.SHEET_TITLE_HOUR
        );
        feiShuExcelCaller.copySheet(client, excelToken, copyMap);
    }

    private void createDaySheet2Map(Client client, String excelToken, String
            offerId, Map<String, Sheet> sheetMap) throws Exception {
        Map<String, String> copyMap = Map.of(
                sheetMap.get(Constants.DAY_TEMPLATE).getSheetId(), offerId + Constants.SHEET_TITLE_DAY
        );
        feiShuExcelCaller.copySheet(client, excelToken, copyMap);
    }

    private void feiShuBotMsg(Client client, ConfigItem configItem, List<List<Object>> data) throws Exception {

        File file = imageService.genImage(data);

        CreateImageReq req = CreateImageReq.newBuilder()
                .createImageReqBody(CreateImageReqBody.newBuilder()
                        .imageType("message")
                        .image(file)
                        .build())
                .build();

        // 发起请求
        CreateImageResp resp = client.im().v1().image().create(req);
        // 处理服务端错误
        if (!resp.success()) {
            LOGGER.warning(OBJECTMAPPER.writeValueAsString(resp));
            return;
        }
        feiShuExcelCaller.feiShuBotMsg(configItem, configItem.getName());
        feiShuExcelCaller.feiShuBotImageMsg(configItem, resp.getData().getImageKey());
    }


    private void productInfo(ConfigItem configItem, Map<String, GoodAnalysis> skuMap) throws IOException {
        List<String> skuList = skuMap.values().stream().map(GoodAnalysis::getSku).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(skuList)) {
            return;
        }
        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        ProductInfoRequest productInfoRequest = ProductInfoRequest.builder()
                .sku(skuList)
                .build();
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(productInfoRequest));

        Request request = new Request.Builder()
                .url(Constants.PRODUCT_REQUEST)
                .method(Constants.POST, body)
                .addHeader(Constants.API_KEY, configItem.getApiKey())
                .addHeader(Constants.CLIENT_ID, configItem.getClientIdP())
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();
        Response response = CLIENT.newCall(request).execute();

        if (response.body() != null) {
            String responseStr = response.body().string();
            LOGGER.info(responseStr);
            ProductInfoResponse productInfoResponse = OBJECTMAPPER.readValue(responseStr, ProductInfoResponse.class);
            for (ProductInfoItem item : productInfoResponse.getItems()) {
                if (CollectionUtils.isEmpty(item.getSources())) {
                    LOGGER.warning("不存在的sku：" + item.getOfferId());
                    continue;
                }
                String sku = item.getSources().get(0).getSku().toString();
                if (skuMap.containsKey(sku)) {
                    GoodAnalysis goodAnalysis = skuMap.get(sku);
                    goodAnalysis.setProductId(item.getId().toString());
                    goodAnalysis.setOfferId(item.getOfferId());
                } else {
                    LOGGER.warning("不存在的商品：" + sku);
                }
            }
        }
        response.close();
    }

    private void price(ConfigItem configItem, Map<String, GoodAnalysis> productMap) throws IOException {
        List<String> product = new ArrayList<>(productMap.keySet());
        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        PriceRequest priceRequest = PriceRequest.builder()
                .filter(ListFilter.builder().productId(product).build())
                .limit(1000)
                .build();
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(priceRequest));

        Request request = new Request.Builder()
                .url(Constants.PRICE_REQUEST)
                .method(Constants.POST, body)
                .addHeader(Constants.API_KEY, configItem.getApiKey())
                .addHeader(Constants.CLIENT_ID, configItem.getClientIdP())
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();
        Response response = CLIENT.newCall(request).execute();
//        LOGGER.info(OBJECTMAPPER.writeValueAsString(response));
        if (response.body() != null) {
            String responseStr = response.body().string();
            LOGGER.info(responseStr);
            PriceResponse priceResponse = OBJECTMAPPER.readValue(responseStr, PriceResponse.class);
            for (PriceItem item : priceResponse.getItems()) {
                if (productMap.containsKey(item.getProductId().toString())) {
                    GoodAnalysis goodAnalysis = productMap.get(item.getProductId().toString());
                    goodAnalysis.setMyPrice(item.getPrice().getMarketSellerPrice());
                    goodAnalysis.setBuyPrice(item.getPrice().getMarketPrice());
                }
            }
        }
        response.close();

    }

    private void warehouse(ConfigItem configItem, Map<String, GoodAnalysis> skuMap) throws IOException {
        List<String> skuList = skuMap.values().stream().map(GoodAnalysis::getSku).collect(Collectors.toList());
        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        WarehouseRequest warehouseRequest = WarehouseRequest.builder()
                .sku(skuList)
                .build();
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(warehouseRequest));

        Request request = new Request.Builder()
                .url(Constants.WAREHOUSE_REQUEST)
                .method(Constants.POST, body)
                .addHeader(Constants.API_KEY, configItem.getApiKey())
                .addHeader(Constants.CLIENT_ID, configItem.getClientIdP())
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();
        Response response = CLIENT.newCall(request).execute();

        if (response.body() != null) {
            String responseStr = response.body().string();
            WarehouseResponse warehouseResponse = OBJECTMAPPER.readValue(responseStr, WarehouseResponse.class);
            for (Warehouse warehouse : warehouseResponse.getResult()) {
                String sku = warehouse.getSku().toString();
                if (skuMap.containsKey(sku)) {
                    skuMap.get(sku).setProductId(warehouse.getProductId().toString());
                } else {
                    LOGGER.warning("不存在的商品：" + sku);
                }
            }
        }
        response.close();
    }

    private void analysis(String today, ConfigItem configItem, List<GoodAnalysis> goodAnalysisList) throws
            IOException {
        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        AnalyticsRequest analyticsRequest = AnalyticsRequest.builder()
                .dateFrom(today)
                .dateTo(today)
                .dimension(List.of("sku", "day"))
                .metrics(AnalyticsEnums.getCond())
                .limit(1000)
                .offset(0)
                .build();
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(analyticsRequest));

        Request request = new Request.Builder()
                .url(Constants.ANALYTICS_DATA_REQUEST)
                .method(Constants.POST, body)
                .addHeader(Constants.API_KEY, configItem.getApiKey())
                .addHeader(Constants.CLIENT_ID, configItem.getClientIdP())
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();
        Response response = CLIENT.newCall(request).execute();
        if (response.body() != null) {
            String responseStr = response.body().string();
            AnalyticsResponse analyticsResponse = OBJECTMAPPER.readValue(responseStr, AnalyticsResponse.class);
            if (analyticsResponse.getResult() == null || analyticsResponse.getResult().getData() == null) {
                LOGGER.warning("没有查到数据"+ responseStr);
                return;
            }
            for (AnalyticsData data : analyticsResponse.getResult().getData()) {
                GoodAnalysis goodAnalysis = GoodAnalysis.builder().sku(data.getDimensions().get(0).getId())
                        .hitsView(data.getMetrics().get(0).intValue())
                        .sessionViewPdp(data.getMetrics().get(1).intValue())
                        .revenue(data.getMetrics().get(2).intValue())
                        .orderedUnits(data.getMetrics().get(3).intValue())
                        .hitsTocart(data.getMetrics().get(4).intValue())
                        .positionCategory(data.getMetrics().get(5).intValue())
                        .cancellations(data.getMetrics().get(6).intValue())
                        .returns(data.getMetrics().get(7).intValue())
                        .build();
                // 过滤掉浏览次数小于2的商品，因为浏览次数小于2的商品，没有参考价值。
                if (goodAnalysis.getSessionViewPdp() > 1) {
                    goodAnalysisList.add(goodAnalysis);
                }
            }
        }
        response.close();
    }
}
