package tw.brad.apps.cloudfitness.java_class;

import android.content.Intent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.brad.apps.cloudfitness.java_class.data.Record;

/**
 * Created by EdLo on 2018/3/15.
 */

public class Algorithm {
    public static String imperialToMetric(String ft, String in){
        int temp_ft = Integer.parseInt(ft);
        int temp_in;
        if(in != null){
            temp_in = Integer.parseInt(in);
        }else {
            temp_in = 0;
        }
        return ""+(Math.round((3048 * temp_ft + 254 * temp_in)/100));
    }

    public static String[] metricToImperial(String cm){
        int tmp_cm = Integer.parseInt(cm);
        int ft = tmp_cm*100/3048;
        long in = Math.round(((tmp_cm * 100 - ft * 3048) * 10/254 + 5)/10);
        if(in >= 12){
            in = 0;
            ft++;
        }
        return new String[]{""+ft, ""+in};
    }

    public static double poundToKg(double lbs) {
        return Math.round((lbs * .454) * 10) /10.0;
    }

    public static double kgToPound(double kg) {
        DecimalFormat df = new DecimalFormat("##.0");
        return Double.parseDouble(df.format((kg*22046/1000+5)/10));
    }

    public long[] getTimeStamp(List<Record> records, String period){
        Calendar calendar = Calendar.getInstance();
        Date date_start = new Date(records.get(records.size()-1).getDateTime());
        long time_end = date_start.getTime();
        calendar.setTime(date_start);
        long time_start;
        switch (period){
            case "day":
                DateFormat format_day = new SimpleDateFormat("yyyy/MM/dd");
                String date_String = format_day.format(calendar.getTime());
                String[] mDate = date_String.split("/");
                int year = Integer.parseInt(mDate[0]);
                int month = Integer.parseInt(mDate[1]);
                int date = Integer.parseInt(mDate[2]);
                calendar.set(year, month -1, date, 0, 0, 0);
                break;
            case "week":
                calendar.add(Calendar.DAY_OF_MONTH, -6);
                break;
            case "month":
                calendar.add(Calendar.DAY_OF_MONTH, -27);
                break;
            case "year":
                DateFormat format_year = new SimpleDateFormat("yyyy/MM/");
                calendar.add(Calendar.MONTH, -11);
                String tmp = format_year.format(calendar.getTime());
                String tmpTime = tmp.substring(0, tmp.lastIndexOf("/"));
                String[] s = tmpTime.split("/");
                year = Integer.parseInt(s[0]);
                month = Integer.parseInt(s[1]);
                calendar.set(year, month -1, 1);
                break;
        }
        time_start = calendar.getTimeInMillis();
        return new long[]{time_start, time_end};
    }

    public static long getAge(String birth) throws ParseException {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date d = format.parse(birth);
        Calendar begin  = Calendar.getInstance();
        begin.setTime(d);
        Calendar now = Calendar.getInstance();
        long years = 0;
        while(begin.before(now)) {
            begin.add(Calendar.YEAR, 1);
            years++;
        }
        return years - 1;
    }

    public static Map<String, Serializable> getDateDate(List<Record> records){
        Map<String, Serializable> map = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> datas = new ArrayList<>();

        // 先取得最新一筆紀錄的日期標籤
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(records.get(records.size()-1).getDateTime());
        calendar.setTime(date);

        // 尋訪Record list 將符合時間標籤的紀錄抓出來
        DateFormat format_time = new SimpleDateFormat("hh:mm");
        for (Record record : records) {
            // 取得時間格式 hh:mm  當作圖表的橫軸
            labels.add(format_time.format(record.getDateTime()));
            // 取的體重紀錄
            datas.add(record.getWeight());
        }
        map.put("label", labels.toArray());
        map.put("data", datas.toArray());
        return map;
    }

    public static Map<String, Serializable> getWeekDate(List<Record> records){
        Map<String, Serializable> map = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> datas = new ArrayList<>();

        // 先取得最新一筆紀錄的日期標籤
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(records.get(records.size()-1).getDateTime());
        calendar.setTime(date);

        String[] mWeek = new String[] {"Mon", "Tue", "Wed", "Thr", "Fri", "Sat", "Sun"};
        DateFormat format_week = new SimpleDateFormat("u");
        List<String> week = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            // 取得過去七天的日期標籤
            String tmp_date = format.format(calendar.getTime());
            week.add(tmp_date);

            // 取得過去七天的day_of_week
            String tmp_week = format_week.format(calendar.getTime());
            int day_of_week = Integer.parseInt(tmp_week);
            // 將day_of_week寫入 作為橫軸
            labels.add(mWeek[day_of_week - 1]);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }


        // 尋訪Record list 將符合時間標籤的紀錄抓出來
        List<List<Double>> week_weight = new ArrayList<>();
        for (int i = 0; i < week.size(); i++) {
            List<Double>  day_weight = new ArrayList<>();
            String week_stamp = week.get(i);
            for (Record record : records) {
                String tmp = format.format(record.getDateTime());
                if (tmp.equals(week_stamp)){
                    // 取的體重紀錄
                    day_weight.add(record.getWeight());
                }
            }
            week_weight.add(day_weight);
        }
        getAvgWeight(week_weight, datas);
        map.put("label", labels.toArray());
        map.put("data", datas.toArray());
        return map;
    }

    public static Map<String, Serializable> getMonthDate(List<Record> records){
        Map<String, Serializable> map = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> datas = new ArrayList<>();

        // 先取得最新一筆紀錄的日期標籤
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(records.get(records.size()-1).getDateTime());
        calendar.setTime(date);

        String[] mMonth =
                new String[] {
                        "1-1", "1-3", "1-5", "1-7",
                        "2-1", "2-3", "2-5", "2-7",
                        "3-1", "3-3", "3-5", "3-7",
                        "4-1", "4-3", "4-5", "4-7"
                };
        DateFormat format_month = new SimpleDateFormat("yyyy/MM/dd");
        List<String> week = new ArrayList<>();
        for(int i = 0; i < 28; i++) {
            // 取得過去28天的日期標籤
            String tmp_date = format.format(calendar.getTime());
            week.add(tmp_date);

            // 寫入橫軸資料
            labels.add(mMonth[i]);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }


        // 尋訪Record list 將符合時間標籤的紀錄抓出來
        List<List<Double>> week_weight = new ArrayList<>();
        for (int i = 0; i < week.size(); i++) {
            List<Double>  day_weight = new ArrayList<>();
            String week_stamp = week.get(i);
            for (Record record : records) {
                String tmp = format.format(record.getDateTime());
                if (tmp.equals(week_stamp)){
                    // 取的體重紀錄
                    day_weight.add(record.getWeight());
                }
            }
            week_weight.add(day_weight);
        }
        getAvgWeight(week_weight, datas);
        map.put("label", labels.toArray());
        map.put("data", datas.toArray());
        return map;
    }

    private static void getAvgWeight(List<List<Double>> week_weight, List<Double> datas){
        DecimalFormat df = new DecimalFormat("##.0");
        for (int i = 0; i < week_weight.size(); i++){
            List<Double> day_weights = week_weight.get(i);
            double sum = 0;
            for (int j = 0; j < day_weights.size(); j++){
                sum += day_weights.get(i);
            }
            double avg_weight = Double.parseDouble(df.format(sum/day_weights.size()));
            datas.add(avg_weight);
        }

    }
}
