package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utility {

    Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public double dpToPx(double dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp, context.getResources().getDisplayMetrics());
    }

    public double pxToDp(double px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public double spToPx(double sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (float) sp, context.getResources().getDisplayMetrics());
    }

    public static int getCurrentTime() {
        Calendar calendar = Calendar.getInstance(Locale.JAPAN);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    public static String getAppbarTitle() {
        Calendar calendar = Calendar.getInstance();
        return String.format(
                "%d年%d月%d日(%s)",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE),
                getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK))
        );
    }

    public static String getDayOfWeekString(int dayOfWeek) {
        String[] dayOfWeeks = new String[]{null, "日", "月", "火", "水", "木", "金", "土"};
        return dayOfWeeks[dayOfWeek];
    }

    public static Date getNextRoundedTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static boolean isSameDate(Calendar calendar0, Calendar calendar1) {
        int[] constants = new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DATE};
        boolean isSameDate = true;
        for (int constant : constants) {
            if (calendar0.get(constant) != calendar1.get(constant)) {
                isSameDate = false;
            }
        }
        return isSameDate;
    }

    // 490, true -> "8時間10分"
    // 490, false -> "08:10"
    public static String convertTime(int time, boolean isTimeLeft) {
        if (time == Constants.NEXT_MORNING_INT) {
            return Constants.NEXT_MORNING_STRING;
        }
        if (time == -1) {
            return "";
        }
        int hour = time / 60;
        int minute = time % 60;
        if (isTimeLeft && hour == 0) {
            return String.format("%d分", minute);
        } else {
            String format = isTimeLeft ? "%d時間 %d分" : "%02d:%02d";
            return String.format(format, hour, minute);
        }
    }

    // "08:10" or "8:10" -> 490
    public static int convertTime(String time) {
        if (time.equals(Constants.NEXT_MORNING_STRING)) {
            return Constants.NEXT_MORNING_INT;
        }
        if (!Pattern.matches(".*[0-9].*", time)) {
            return -1;
        }
        //int flat = Integer.parseInt(time.replace(":", "").replace("：", "").replace(" ", ""));
        int flat = Integer.parseInt(time.replaceAll("[^0-9]", "")); // 生協の入力ミスのせい

        return flat / 100 * 60 + flat % 100;
    }

    // int -> int[]{hourOfDay, minute}
    public static int[] convertTimeForCalendar(int time) {
        if (time == Constants.NEXT_MORNING_INT) {
            return new int[]{Constants.NEXT_MORNING_INT, Constants.NEXT_MORNING_INT};
        }
        if (time == -1) {
            return new int[]{-1, -1};
        }
        return new int[]{time / 60, time % 60};
    }

    // int[]{hourOfDay, minute} -> int
    public static int convertTimeFromCalendar(int[] timeForCalendar) {
        if (timeForCalendar[0] == -1 || timeForCalendar[1] == -1) {
            return -1;
        }
        return timeForCalendar[0] * 60 + timeForCalendar[1];
    }

    // "490,550" -> {490, 550}
    public static int[] convertTimes(String timesInString) {
        if (timesInString.equals("")) {
            return new int[]{-1, -1};
        }
        String[] dividedTimesInString = timesInString.split(",");
        int[] dividedTimes = new int[dividedTimesInString.length];
        for (int i = 0; i < dividedTimesInString.length; i++) {
            if (dividedTimesInString[i].equals(Constants.NEXT_MORNING_STRING)) {
                dividedTimes[i] = Constants.NEXT_MORNING_INT;
            } else {
                dividedTimes[i] = Integer.parseInt(dividedTimesInString[i]);
            }
        }
        return dividedTimes;
    }

    // {490, 550}, true -> "8:10-9:10"
    // {490, 550}, false -> "490,550"
    public static String convertTimes(int[] timesInInt, boolean isForShow) {
        if (timesInInt[0] == -1) {
            return "";
        }
        String times = "";
        for (int i = 0; i < timesInInt.length; i++) {
            times += isForShow ? convertTime(timesInInt[i], false) : String.valueOf(timesInInt[i]);
            if (i < timesInInt.length - 1) {
                times += isForShow ? "-" : ",";
            }
        }
        return times;
    }

    // "8:10～9:10" or "8:10-9:10" -> {490, 550}
    public static int[] parseTimes(String timesInString) {
        if (!Pattern.matches(".*[0-9].*", timesInString)) {
            return new int[]{-1, -1};
        }
        timesInString = timesInString.replace(" ", "").replace("　", "");

        String[] stringTimes = null;
        String[] dividers = new String[]{"～", "-"};
        for (String divider : dividers) {
            if (timesInString.contains(divider)) {
                stringTimes = timesInString.split(divider);
            }
        }

        int[] times = new int[stringTimes.length];
        for (int i = 0; i < stringTimes.length; i++) {
            if (Pattern.matches(".*[0-9].*", stringTimes[i])) {
                times[i] = convertTime(stringTimes[i]);
            } else {
                // "翌朝"であると断定
                times[i] = Constants.NEXT_MORNING_INT;
            }
        }
        return times;
    }

    public static boolean isFuture(int time) {
        return time == Constants.NEXT_MORNING_INT || time >= getCurrentTime();
    }

    public static double widthBetweenTimes(int earlier, int latter) {
        if (latter == Constants.NEXT_MORNING_INT) {
            return widthBetweenTimes(earlier, Constants.MAX_MAX_TIME);
        }
        return Constants.WIDTH_PER_MINUTE * (double) (latter - earlier);
    }

    public static String normalize(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFKC);
    }

    public static boolean isSameDirection(List<String> direction, String[] locations) {
        return direction.get(0).equals(locations[0])
                && direction.get(direction.size() - 1).equals(locations[locations.length - 1]);
    }

    public static CharSequence shortenSectionText(String section) {
        String[] locations = section.split("⇔");
        for (int i = 0; i < locations.length; i++) {
            locations[i] = locations[i].substring(0, 1);
        }
        String sectionText = TextUtils.join("<SMALL>-</SMALL>", locations);
        return Html.fromHtml(sectionText);
    }

    public static Spanned makeDirectionText(List<String> locationList, boolean longVersion) {
        String directionText = "";
        for (int i = 0; i < locationList.size(); i++) {
            directionText
                    += (longVersion ? locationList.get(i) : locationList.get(i).substring(0, 1))
                    + (i < locationList.size() - 1 ? (longVersion ? " → " : "<SMALL>&gt;</SMALL>") : "");
        }
        return Html.fromHtml(directionText);
    }

    public static String makeSnackbarText(String name, int[] times, boolean isLibrary) {
        String timeText;
        if (times[0] == -1) {
            timeText = isLibrary ? "休館" : "休業";
        } else {
            timeText = convertTimes(times, true);
        }
        return name
                + (isLibrary ? "図書館" : "")
                + " "
                + timeText;
    }

    public static boolean isBusDataValid(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month;
    }

    public static int[] parseMonthAndDate(String monthAndDateInString) {
        int splitIndex = monthAndDateInString.indexOf("月") + 1;
        int month = Integer.parseInt(monthAndDateInString.substring(0, splitIndex - 1));
        int date = Integer.parseInt(monthAndDateInString.substring(splitIndex, monthAndDateInString.length() - 1));
        return new int[]{month, date};
    }

    public static String makeMapUriString(String latitude, String longitude) {
        return "google.navigation:q=" + latitude + "," + longitude + "&mode=w";
    }
}
