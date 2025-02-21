package com.ting.shop.bot.caller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.service.sheets.v3.model.Find;
import com.lark.oapi.service.sheets.v3.model.FindCondition;
import com.lark.oapi.service.sheets.v3.model.FindSpreadsheetSheetReq;
import com.lark.oapi.service.sheets.v3.model.FindSpreadsheetSheetResp;
import com.ting.shop.bot.caller.feishu.*;
import com.ting.shop.bot.common.Constants;
import com.ting.shop.bot.config.ConfigItem;
import com.ting.shop.bot.task.AnalysisTask;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Classname FeiShuCaller
 * @Description TODO
 * @Date 2025/1/1 09:06
 * @Author by chenlt
 */
@Component
public class FeiShuExcelCaller {

    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder().build();
    private static final Logger LOGGER = Logger.getLogger(AnalysisTask.class.getName());

    /**
     * 飞书机器人消息
     *
     * @param configItem 配置
     */
    public void feiShuBotImageMsg(ConfigItem configItem, String imageKey) throws IOException {
        FeiShuBotMsgRequest feiShuBotRequest = FeiShuBotMsgRequest.builder()
                .msgType(Constants.IMAGE_MSG_TYPE)
                .content(TextContent.builder().imageKey(imageKey).build())
                .build();

        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(feiShuBotRequest));

        Request request = new Request.Builder()
                .url(Constants.FEI_SHU_HOOK + configItem.getFeiShuWebhook())
                .method(Constants.POST, body)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();

        CLIENT.newCall(request).execute();

    }

    /**
     * 飞书机器人消息
     *
     * @param configItem 配置
     */
    public void feiShuBotMsg(ConfigItem configItem, String content) throws IOException {
        FeiShuBotMsgRequest feiShuBotRequest = FeiShuBotMsgRequest.builder()
                .msgType(Constants.TEXT_MSG_TYPE)
                .content(TextContent.builder().text(content).build())
                .build();

        MediaType mediaType = MediaType.parse(Constants.CONTENT_TYPE_JSON);
        RequestBody body = RequestBody.create(mediaType, OBJECTMAPPER.writeValueAsBytes(feiShuBotRequest));

        Request request = new Request.Builder()
                .url(Constants.FEI_SHU_HOOK + configItem.getFeiShuWebhook())
                .method(Constants.POST, body)
                .addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .build();

        CLIENT.newCall(request).execute();

    }

    /**
     * 获取最后一个行号
     *
     * @param client     飞书客户端
     * @param excelToken 飞书excelToken
     * @param sheetId    飞书sheetId
     * @param range      范围
     * @param content    内容
     */
    public int getLastRow(Client client, String excelToken, String sheetId, String range, String content) throws Exception {
        // 创建请求对象
        FindSpreadsheetSheetReq req = FindSpreadsheetSheetReq.newBuilder()
                .spreadsheetToken(excelToken)
                .sheetId(sheetId)
                .find(Find.newBuilder()
                        .findCondition(FindCondition.newBuilder()
                                .range(sheetId + "!" + range)
                                .build())
                        .find(content)
                        .build())
                .build();

        // 发起请求
        FindSpreadsheetSheetResp resp = client.sheets().spreadsheetSheet().find(req);
        if (!resp.success()) {
            LOGGER.warning("服务端错误: " + resp.getCode() + ", " + resp.getMsg());
            return 0;
        }
        String[] matchedCells = resp.getData().getFindResult().getMatchedCells();
        if (matchedCells.length == 0) {
            return 1;
        }
        return Integer.parseInt(matchedCells[matchedCells.length - 1].substring(1));
    }

    /**
     * 复制sheet
     *
     * @param client     飞书客户端

     * @param copyMap    sheetId和title的映射
     */
    public void copySheet(Client client, String excelToken, Map<String, String> copyMap) throws Exception {
        List<CopySheetRequest> requests = new ArrayList<>(copyMap.size());
        // 组装请求对象
        for (Map.Entry<String, String> entry : copyMap.entrySet()) {
            requests.add(CopySheetRequest.builder()
                    .copySheet(CopySheet.builder()
                            .source(SheetSource.builder().sheetId(entry.getKey()).build())
                            .destination(SheetDestination.builder().title(entry.getValue()).build())
                            .build()
                    ).build());
        }
        FeiShuExcelSheetOperaRequest feiShuExcelSheetOperaRequest = FeiShuExcelSheetOperaRequest.builder().requests(requests).build();
        client.post(Constants.FEI_SHU_SHEET_OPERATE.replace(Constants.SPREADSHEET_TOKEN, excelToken), feiShuExcelSheetOperaRequest, AccessTokenType.Tenant);
    }

    /**
     * 设置多个单元格的值
     *
     * @param client           飞书客户端
     * @param feiShuExcelToken 飞书excelToken
     * @param valueMap         单元格的值
     */
    public void setMultiValues(Client client, String feiShuExcelToken, Map<String, List<List<Object>>> valueMap) throws Exception {
        List<ValueRange> valueRanges = new ArrayList<>();
        for (Map.Entry<String, List<List<Object>>> entry : valueMap.entrySet()) {
            valueRanges.add(ValueRange.builder().range(entry.getKey()).values(entry.getValue()).build());
        }
        MultiRangeSetRequest rangeSetRequest = MultiRangeSetRequest.builder().valueRanges(valueRanges).build();
        System.out.println(OBJECTMAPPER.writeValueAsString(rangeSetRequest));

        client.post(Constants.FEI_SHU_VALUES_BATCH_UPDATE.replace(Constants.SPREADSHEET_TOKEN, feiShuExcelToken), rangeSetRequest, AccessTokenType.Tenant);
    }


    /**
     * 设置多个单元格的值
     *
     * @param client           飞书客户端
     * @param feiShuExcelToken 飞书excelToken
     */
    public int valuesAppend(Client client, String feiShuExcelToken, ValueRange valueRange) throws Exception {

        ValuesAppendRequest rangeSetRequest = ValuesAppendRequest.builder().valueRange(valueRange).build();

        RawResponse response = client.post(Constants.FEI_SHU_VALUES_APPEND.replace(Constants.SPREADSHEET_TOKEN, feiShuExcelToken), rangeSetRequest, AccessTokenType.Tenant);
        ValuesAppendResponse valuesAppendResponse = OBJECTMAPPER.readValue(response.getBody(), ValuesAppendResponse.class);
        if (valuesAppendResponse.getCode() == 0) {
            return Integer.parseInt(valuesAppendResponse.getData().getTableRange().split(":")[1].substring(1));
        }
        return 0;
    }


}
