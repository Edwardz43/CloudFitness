package tw.brad.apps.cloudfitness;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    private CombinedChart mChart;
    private int itemcount = 12;
    private  String[] mSelectedType;
    protected String[] mYear = new String[] {"Nov", "Dec", "Jan"};//"Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt",
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

        // 初始化圖表
        initChart();
    }

    private void initChart() {
        float minYAxisValue;

        // 根據按鈕來決定資料呈現 : 日 周 月 年
        switch (mType){
            case DAY:
                mSelectedType = mDay;
                dataSet = new double[] {158.5, 158.4, 159};
                itemcount = mSelectedType.length;
                minYAxisValue = (float) 158;
                break;

            case WEEK:
                mSelectedType = mWeek;
                dataSet = new double[] {
                        157.3,
                        157.6,
                        157.8,
                        158.1,
                        158.3,
                        158.6,
                        159.5
                };
                itemcount = mSelectedType.length;
                minYAxisValue = (float) 157;
                break;

            case MONTH:
                mSelectedType = mMonth;
                dataSet = new double[] {
                        155.3,
                        155.2,
                        155,
                        155.5,
                        155,
                        156.3,
                        157.1,
                        156.3,
                        156.5,
                        157.1,
                        157.9,
                        157.5,
                        157.3,
                        157.8,
                        158.3,
                        159.5
                };
                itemcount = mSelectedType.length;
                minYAxisValue = (float) 155;
                break;

            case YEAR:
                mSelectedType = mYear;
                dataSet = new double[] {156, 156.2, 157.1};
                itemcount = mSelectedType.length;
                minYAxisValue = (float) 155;
                break;
            default:
                mType = DAY;
                mSelectedType = mDay;
                dataSet = new double[] {158.5, 158.4, 159};
                itemcount = mSelectedType.length;
                minYAxisValue = (float) 158;
        }
        mChart = (CombinedChart) findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        //mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.BUBBLE,
                CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE,
                CombinedChart.DrawOrder.SCATTER
        });
        Legend legend = mChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(minYAxisValue); // this replaces setStartAtZero(true)

        //設定縱軸
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(minYAxisValue); // this replaces setStartAtZero(true)

        //設定橫軸
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(itemcount);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mSelectedType[(int) value % itemcount];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());
        data.setValueTypeface(mTfLight);

        xAxis.setAxisMaximum(data.getXMax());

        mChart.setData(data);
        mChart.invalidate();
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(index + 0.1f, (float)dataSet[index]));

        LineDataSet set = new LineDataSet(entries, "Line Data");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        //ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int index = 0; index < itemcount; index++) {
            entries1.add(new BarEntry(index + 0.1f, (float)dataSet[index]));
            Log.i("Chart", "entries1 : " + entries1.get(index).getX() +", " + entries1.get(index).getY() );
        }

        BarDataSet set1 = new BarDataSet(entries1, "Bar Data");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        //set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

//        float groupSpace = 0.06f;
//        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.5f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData barData = new BarData(set1);
        barData.setBarWidth(barWidth);

        // make this BarData object grouped
        //barData.groupBars(0, groupSpace, 0); // start at x = 0

        return barData;
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
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
    }

    // 按鈕 : 日資料
    public void show_week(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnWeek.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnWeek.setTextColor(0);
        mType = WEEK;
        initChart();
    }

    // 按鈕 : 日資料
    public void show_month(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnMonth.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnMonth.setTextColor(0);
        mType = MONTH;
        initChart();
    }

    // 按鈕 : 日資料
    public void show_year(View view){
        for (Button btn: buttonList) {
            btn.setBackgroundResource(R.drawable.border_pinkblue_buttons);
            //btn.setTextColor(255);
        }
        btnYear.setBackgroundResource(R.drawable.border_lightgreen_buttons);
        //btnYear.setTextColor(0);
        mType = YEAR;
        initChart();
    }

    // 按下返回鍵 : 回到 LastWeightActivity
    public void back(View view){
        finish();
    }


    // 設定Y軸顯示的最小值
    private float setMinYAxisValue(double[] mData){
        //TODO
        //排序後 取出最小值  目前暫時回傳0
        return 0f;
    }
}
