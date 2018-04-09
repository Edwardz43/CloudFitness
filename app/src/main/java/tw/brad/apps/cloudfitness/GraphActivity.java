package tw.brad.apps.cloudfitness;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tw.brad.apps.cloudfitness.chart.DayAxisValueFormatter;
import tw.brad.apps.cloudfitness.chart.MyAxisValueFormatter;
import tw.brad.apps.cloudfitness.chart.XYMarkerView;
import tw.brad.apps.cloudfitness.java_class.Algorithm;
import tw.brad.apps.cloudfitness.java_class.data.MyDBHelper;
import tw.brad.apps.cloudfitness.java_class.data.Record;
import tw.brad.apps.cloudfitness.java_class.data.User;


public class GraphActivity extends AppCompatActivity {
    private BarChart mChart;
    private int itemcount = 12;
    private  String[] mSelectedType;
    protected String[] mYear = new String[] {"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt","Nov", "Dec", "Jan","Feb", "Mar"};
    protected String[] mMonth =
            new String[] {"1-1", "1-3", "1-5", "1-7", "2-1", "2-3", "2-5", "2-7", "3-1", "3-3", "3-5", "3-7", "4-1", "4-3", "4-5", "4-7"};
    protected String[] mWeek = new String[] {"Sat", "Sun", "Mon", "Tue", "Wed", "Thr", "Fri"};
    protected String[] mDay = new String[] {"13:00", "15:30" ,"19:30"};
    private double[] dataSet;
    protected Typeface mTfLight;
    private String mType;
    private final static String MONTH = "month";
    private final static String WEEK = "week";
    private final static String DAY = "day";
    private final static String YEAR = "year";
    private Button btnDay, btnWeek, btnMonth, btnYear;
    private List<Button> buttonList;
    private List<Record> records;
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private boolean isImperial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_graph);
        init();
    }

    // 初始化
    private void init() {

        // 綁定按鍵
        btnDay = findViewById(R.id.btn_day);
        btnWeek = findViewById(R.id.btn_week);
        btnMonth = findViewById(R.id.btn_month);
        btnYear = findViewById(R.id.btn_year);
        buttonList = new ArrayList<>();

        // 將按鍵加進 list裡
        buttonList.add(btnDay);
        buttonList.add(btnWeek);
        buttonList.add(btnMonth);
        buttonList.add(btnYear);

        // 預設顯示 : 日資料
        mType = DAY;

        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();
        User user = (User) getIntent().getSerializableExtra("user");
        records = Record.query(user.getId(), db);
        isImperial = user.getUnit_type() == 0 ? true : false;
        //Log.d("ed43", new Gson().toJson(records));

        // 初始化圖表
        initChart();
    }

    private void initChart() {
        float minYAxisValue;

        // 根據按鈕來決定資料呈現 : 日 周 月 年
        switch (mType){
            case DAY:
                // 測試 : 拿掉最多5筆限制 最多7筆(last 7)
                int index = records.size() > 7 ? 7 : records.size();
                mSelectedType = new String[index];
                dataSet = new double[index];
                for (int i = 0; i < index; i ++){
                    int n = records.size() - index + i;
                    //Log.d("ed43", "" + n);
                    dataSet[i] = records.get(n).getWeight();
                    DateFormat format = new SimpleDateFormat("HH:mm");
                    String mTime = format.format(records.get(n).getDateTime());
                    mSelectedType[i] = mTime;
                }
                break;

            case WEEK:
                mSelectedType = mWeek;
                dataSet = new double[] {
                        115.3,
                        115.6,
                        114.8,
                        115.1,
                        115.3,
                        114.6,
                        114.5
                };
                break;

            case MONTH:
                mSelectedType = mMonth;
                dataSet = new double[] {
                        115.3,
                        115.2,
                        0,
                        115.5,
                        0,
                        116.3,
                        116.1,
                        116.3,
                        115.5,
                        116.1,
                        114.9,
                        115.5,
                        115.3,
                        114.8,
                        115.3,
                        115.5
                };
                break;

            case YEAR:
                mSelectedType = mYear;
                dataSet = new double[] {
                        115.4,
                        116.2,
                        116.1,
                        115.5,
                        115.1,
                        114.9,
                        115.5,
                        116.3,
                        115.8,
                        115.3,
                        115.5,
                        116.2
                };
                break;
            default:
                mType = DAY;
                mSelectedType = mDay;
                dataSet = new double[] {115.5, 115.4, 116};
        }

        itemcount = mSelectedType.length;
        if(isImperial){
            minYAxisValue = (float) Algorithm.kgToPound(setMinYAxisValue(dataSet));
        }else {
            minYAxisValue = (float) setMinYAxisValue(dataSet);
        }

        mChart = (BarChart) findViewById(R.id.chart);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);
        IAxisValueFormatter custom = new MyAxisValueFormatter();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mSelectedType[(int) value % itemcount];
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(minYAxisValue); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(minYAxisValue); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        XYMarkerView mv = new XYMarkerView(this, new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mSelectedType[(int) value % itemcount];
            }
        });
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        setData(itemcount, 200);
    }

    private void setData(int count, float range) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0 ; i < count; i++) {
            double mWeight = 0.0;
            if(isImperial){
                mWeight = Algorithm.kgToPound(dataSet[i]);
            }else {
                mWeight = dataSet[i];
            }
            yVals1.add(new BarEntry(i, (float)mWeight));
        }
        BarDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            // 如果資料太少 為了美觀  把Bar的寬度調低
            if(itemcount < 4){
                data.setBarWidth(0.5f);
            }
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    DecimalFormat mFormat = new DecimalFormat("##.0");
                    return mFormat.format(value);
                }
            });
            mChart.setData(data);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Weight");
            set1.setDrawIcons(false);
            set1.setColors(Color.GREEN);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            // 如果資料太少 為了美觀  把Bar的寬度調低
            if(itemcount < 4){
                data.setBarWidth(0.5f);
            }
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    DecimalFormat mFormat = new DecimalFormat("##.0");
                    return mFormat.format(value);
                }
            });
            mChart.setData(data);
        }
    }

    // 按鈕 : 日資料
    public void show_day(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnDay.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnDay.setTextColor(0);
        mType = DAY;
        initChart();
        mChart.invalidate();
    }

    // 按鈕 : 周資料
    public void show_week(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnWeek.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnWeek.setTextColor(0);
        mType = WEEK;
        initChart();
        mChart.invalidate();
    }

    // 按鈕 : 月資料
    public void show_month(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnMonth.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnMonth.setTextColor(0);
        mType = MONTH;
        initChart();
        mChart.invalidate();
    }

    // 按鈕 : 年資料
    public void show_year(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnYear.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnYear.setTextColor(0);
        mType = YEAR;
        initChart();
        mChart.invalidate();
    }

    // 按下返回鍵 : 回到 LastWeightActivity
    public void back(View view){
        finish();
    }


    // 設定Y軸顯示的最小值
    private double setMinYAxisValue(double[] mData){
        //TODO
        //排序後 取出最小值
        double[] tmp = mData.clone();
        Arrays.sort(tmp);
        double result = mData[0] - 1;
        //Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
        return result;
    }
}
