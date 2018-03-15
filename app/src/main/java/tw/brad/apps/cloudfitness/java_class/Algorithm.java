package tw.brad.apps.cloudfitness.java_class;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static double[] year_Data(){
        return new double[]{};
    }

    public static double[] month_Data(){
        return new double[]{};
    }

    public static double[] week_Data(){
        return new double[]{};
    }

    public static double[] day_Data(){
        return new double[]{};
    }
}
