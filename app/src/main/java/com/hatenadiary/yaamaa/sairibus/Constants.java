package com.hatenadiary.yaamaa.sairibus;

import android.graphics.Color;

public class Constants {

    public static final int EDIT_CLASSWORK_REQUEST_CODE = 0;

    public static final int NEXT_MORNING_INT = -2;
    public static final String NEXT_MORNING_STRING = "翌朝";

    // unit: minute
    public static final int ORIGINAL_MIN_TIME = Utility.convertTime("10:00");
    public static final int ORIGINAL_MAX_TIME = Utility.convertTime("18:00");
    public static final int MIN_MIN_TIME = Utility.convertTime("0:00");
    public static final int MAX_MAX_TIME = Utility.convertTime("24:00");
    public static int MIN_TIME = ORIGINAL_MIN_TIME;
    public static int MAX_TIME = ORIGINAL_MAX_TIME;

    public static void clearTimeRange() {
        MIN_TIME = ORIGINAL_MIN_TIME;
        MAX_TIME = ORIGINAL_MAX_TIME;
    }

    public static void entryTimeRange(int start, int end) {
        MIN_TIME = Math.min(MIN_TIME, start);
        MAX_TIME = Math.max(MAX_TIME, end == NEXT_MORNING_INT ? MAX_MAX_TIME : end);
    }

    public static void settleTimeRange() {
        MIN_TIME = (MIN_TIME - 1) / 60 * 60;
        MAX_TIME = MAX_TIME / 60 * 60 + 60;

        MIN_TIME = Math.max(MIN_TIME, MIN_MIN_TIME);
        MAX_TIME = Math.min(MAX_TIME, MAX_MAX_TIME);

        CONTENT_WIDTH = Constants.HORIZONTAL_MARGIN * 2 + Utility.widthBetweenTimes(Constants.MIN_TIME, Constants.MAX_TIME);
    }

    // general
    public static final int REDRAW_SPAN = 60 * 1000;    // unit: millisecond
    public static final int RULE_COUNT_PER_HOUR = 5;
    public static final int BUS_MIN_MARGIN = 20;

    // size in general
    public static final double WIDTH_PER_MINUTE = 2.0;
    public static final double HORIZONTAL_MARGIN = 20.0;    // margin before min_time and after max_time
    public static double CONTENT_WIDTH = 0.0;
    public static final double MARGIN_BETWEEN_ROWS = 8.0;   // xml共通

    // CurrentTimeView
    public static final double MARGIN_HEIGHT_FOR_CURRENT_TIME = 15.0;    // xml共通
    public static final double CURRENT_TIME_CIRCLE_RADIUS = 4.0;
    public static final double CURRENT_TIME_LINE_THICKNESS = 1.5;

    // TimeView
    public static final double TIME_HEIGHT = 20.0;    // xml共通
    public static final double TIME_FONT_SIZE = 13.0;   // unit: sp

    // RowTitle
    public static final double MAX_ROW_TITLE_WIDTH = 120.0;
    public static double ROW_TITLE_WIDTH = MAX_ROW_TITLE_WIDTH;

    public static void setRowTitleWidth(double screenWidth) {
        ROW_TITLE_WIDTH = Math.min(MAX_ROW_TITLE_WIDTH, screenWidth / 4.5);
    }

    public static final double ROW_MAIN_TITLE_HEIGHT = 15.0;
    public static final double ROW_MAIN_TITLE_FONT_SIZE = ROW_MAIN_TITLE_HEIGHT - 3;    // unit: dp
    public static final double ROW_SUBTITLE_FONT_SIZE = ROW_MAIN_TITLE_FONT_SIZE - 2;   // unit: dp

    // PeriodView
    public static final double PERIOD_VIEW_HEIGHT = 25.0;
    public static final double PERIOD_VIEW_VERTICAL_MARGIN = 2.0;   // between top and text, text and line, line and bottom
    public static final double PERIOD_VIEW_FILL_HEIGHT = 6.0;
    public static final double PERIOD_VIEW_LABEL_FONT_SIZE
            = PERIOD_VIEW_HEIGHT - PERIOD_VIEW_VERTICAL_MARGIN * 3 - PERIOD_VIEW_FILL_HEIGHT;    // unit: dp

    // BusView
    public static final double BUS_VIEW_HEIGHT = 18.0;
    public static final double BUS_VIEW_CIRCLE_RADIUS = 4.0;
    public static final double BUS_VIEW_LINE_THICKNESS = 1.8;
    public static final double BUS_VIEW_MARGIN_BETWEEN_LINES = 1.0;
    public static final double BUS_VIEW_MICROBE_THICKNESS = 1.6;
    public static final double BUS_VIEW_MICROBE_VERTICAL_OFFSET = 1.5;

    // color
    public static float PAST_ALPHA = 0.6f;
    public static float BUS_HOLIDAY_ALPHA = 0.38f;

    public static int FOCUS_COLOR = Color.argb(30, 0, 0, 0);
    public static int CURRENT_TIME_COLOR = Color.argb(255, 180, 0, 0);

    public static int MAIN_RULE_COLOR = Color.argb(100, 0, 0, 0);
    public static int SUB_RULE_COLOR = Color.argb(50, 0, 0, 0);

    public static int TIME_TEXT_COLOR = Color.argb(180, 0, 0, 0);

    public static int ROW_TITLE_DEFAULT_COLOR = Color.argb(222, 0, 0, 0);

    public static int CLASSWORK_COLOR = MaterialColors.RED_700;
    public static int LIBRARY_COLOR = MaterialColors.GREEN_700;

    public static int[] restaurantColors = new int[]{
            MaterialColors.AMBER_700,
            MaterialColors.INDIGO_700,
            MaterialColors.PINK_700
    };

    public static int[] busSectionColors = new int[]{
            MaterialColors.INDIGO_700,
            MaterialColors.ORANGE_700
    };
    public static int[][] busDirectionColors = new int[][]{
            {MaterialColors.BLUE_700, MaterialColors.PURPLE_700},
            {MaterialColors.AMBER_700, MaterialColors.PINK_700}
    };

    // database
    public static String[] defaultClassworkNames = new String[]{"1限", "2限", "3限", "4限", "5限", "6限"};
    public static String[] defaultClassworkTimes = new String[]{"530,620", "630,720", "780,870", "880,970", "980,1070", "1080,1170"};

    public static String[] mapCoordinateNames = new String[]{"豊中地区", "コンベンション前", "工学部前", "人間科学部前", "外国語学部前"};
    public static String[] mapCoordinateLatitudes = new String[]{"34.805432", "34.817579", "34.8239834", "34.8179930", "34.8524776"};
    public static String[] mapCoordinateLongitudes = new String[]{"135.455220", "135.522371", "135.5232691", "135.5252657", "135.5163789"};

    // URL
    public static String BUS_URL = "http://www.osaka-u.ac.jp/ja/access/bus.html";

    public static String LIBRARY_SOUGOU_URL = "https://www.library.osaka-u.ac.jp/sougou/schedule/";
    public static String LIBRARY_SEIMEI_URL = "https://www.library.osaka-u.ac.jp/seimei/schedule/";
    public static String LIBRARY_RIKOU_URL = "https://www.library.osaka-u.ac.jp/rikou/schedule/";
    public static String LIBRARY_GAIKOKU_URL = "https://www.library.osaka-u.ac.jp/gaikoku/schedule/";

    public static String RESTAURANT_TOYONAKA_URL = "http://www.osaka-univ.coop/info/02_2.html";
    public static String RESTAURANT_SUITA_URL = "http://www.osaka-univ.coop/info/03_2.html";
    public static String RESTAURANT_MINOH_URL = "http://www.osaka-univ.coop/info/04_2.html";

    public static String RESTAURANT_SHOP_URL = "http://www.osaka-u.ac.jp/ja/guide/student/general/welfare.html";

    public static String HOLIDAY_URL = "http://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html";

    public static String[] linkNames = new String[]{
            "学内連絡バス",
            "総合図書館", "生命科学図書館", "理工学図書館", "外国学図書館",
            "豊中キャンパスの食堂", "吹田キャンパスの食堂", "箕面キャンパスの食堂",
            "食堂・売店等の案内"
    };
    public static String[] linkUrls = new String[]{
            BUS_URL,
            LIBRARY_SOUGOU_URL, LIBRARY_SEIMEI_URL, LIBRARY_RIKOU_URL, LIBRARY_GAIKOKU_URL,
            RESTAURANT_TOYONAKA_URL, RESTAURANT_SUITA_URL, RESTAURANT_MINOH_URL,
            RESTAURANT_SHOP_URL
    };

    // package name
    public static final String GOOGLE_MAP_PACKAGE_NAME = "com.google.android.apps.maps";
}
