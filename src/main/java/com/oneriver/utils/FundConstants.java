package com.oneriver.utils;

public final class FundConstants {

    private FundConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    public static final String SORT_ONE_YEAR = "oneyear";
    public static final String SORT_ONE_MONTH = "onemonth";
    public static final String SORT_THREE_MONTHS = "threemonths";
    public static final String SORT_SIX_MONTHS = "sixmonths";
    public static final String SORT_YEAR_CHANGE = "yearchange";
    public static final String SORT_YTD = "ytd";
    public static final String SORT_THREE_YEARS = "threeyears";
    public static final String SORT_FIVE_YEARS = "fiveyears";
    public static final String SORT_FUND_CODE = "fundcode";
    public static final String SORT_FUND_NAME = "fundname";
    public static final String SORT_UMBRELLA_FUND_TYPE = "umbrellafundtype";

    public static final String ES_FIELD_FUND_CODE = "fundCode";
    public static final String ES_FIELD_FUND_NAME = "fundName";
    public static final String ES_FIELD_FUND_NAME_KEYWORD = "fundName.keyword";
    public static final String ES_FIELD_UMBRELLA_FUND_TYPE = "umbrellaFundType";
    public static final String ES_FIELD_RETURN_PERIODS = "returnPeriods";
    public static final String ES_FIELD_ONE_MONTH = "returnPeriods.oneMonth";
    public static final String ES_FIELD_THREE_MONTHS = "returnPeriods.threeMonths";
    public static final String ES_FIELD_SIX_MONTHS = "returnPeriods.sixMonths";
    public static final String ES_FIELD_YEAR_TO_DATE = "returnPeriods.yearToDate";
    public static final String ES_FIELD_ONE_YEAR = "returnPeriods.oneYear";
    public static final String ES_FIELD_THREE_YEARS = "returnPeriods.threeYears";
    public static final String ES_FIELD_FIVE_YEARS = "returnPeriods.fiveYears";

    public static final String EXCEL_EXTENSION_XLSX = ".xlsx";
    public static final String EXCEL_EXTENSION_XLS = ".xls";
    public static final String DEFAULT_EXCEL_FILE = "takasbank-tefas-fon-karsilastirma.xlsx";

    public static final String MSG_FILE_EMPTY = "File cannot be empty";
    public static final String MSG_FILENAME_EMPTY = "File name cannot be empty";
    public static final String MSG_UNSUPPORTED_FORMAT = "Unsupported file format. Supported formats: .xlsx, .xls";
    public static final String MSG_IMPORT_SUCCESS = "Excel import completed successfully";
    public static final String MSG_REINDEX_SUCCESS = "Re-indexing completed successfully";
}