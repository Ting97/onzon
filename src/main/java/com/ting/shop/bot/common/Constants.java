package com.ting.shop.bot.common;

/**
 * @Classname Constants
 * @Description 常量类
 * @Date 2024/12/22 17:37
 * @Author by chenlt
 */
public interface Constants {
    String ANALYTICS_DATA_REQUEST = "https://api-seller.ozon.ru/v1/analytics/data";
    String WAREHOUSE_REQUEST = "https://api-seller.ozon.ru/v1/product/info/stocks-by-warehouse/fbs";
    String PRODUCT_REQUEST = "https://api-seller.ozon.ru/v3/product/info/list";
    String PRICE_REQUEST = "https://api-seller.ozon.ru/v5/product/info/prices";
    String POST = "POST";
    String GET = "GET";
    String API_KEY = "Api-Key";
    String CLIENT_ID = "Client-Id";
    String CONTENT_TYPE = "Content-Type";
    String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    String AUTHORIZATION = "Authorization";
    String BEARER = "Bearer ";

    String IMAGE_MSG_TYPE = "image";
    String TEXT_MSG_TYPE = "text";

    String SHEET_TITLE_HOUR = "(hour)";
    String SHEET_TITLE_DAY = "(day)";

    String DAY_TIME_START = "T00:00:00Z";



    String FEI_SHU_HOOK = "https://open.feishu.cn/open-apis/bot/v2/hook/";
    String DAY_TEMPLATE = "dayTemplate";
    String HOUR_TEMPLATE = "hourTemplate";
    String FEI_SHU_SHEET_OPERATE = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/:spreadsheet_token/sheets_batch_update";
    String FEI_SHU_VALUES_BATCH_UPDATE = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/:spreadsheet_token/values_batch_update";
    String FEI_SHU_VALUES_APPEND = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/:spreadsheet_token/values_append";
    String FEI_SHU_GET_RANGE_VALUE = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/:spreadsheet_token/values/:range";
    String SPREADSHEET_TOKEN = ":spreadsheet_token";
    String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    String TOKEN_REQUEST = "https://api-performance.ozon.ru/api/client/token";
    String ACTIVITY_REQUEST = "https://api-performance.ozon.ru:443/api/client/campaign?advObjectType=SKU&state=CAMPAIGN_STATE_RUNNING";
    String PROMOTION_STATISTICS_REQUEST = "https://api-performance.ozon.ru:443/api/client/statistics/json";
    String SEARCH_STATISTICS_REQUEST = "https://api-performance.ozon.ru:443/api/client/statistic/products/generate/json";


    String REPORT_DOWNLOAD_REQUEST = "https://api-performance.ozon.ru:443/api/client/statistics/report?UUID=";
}
