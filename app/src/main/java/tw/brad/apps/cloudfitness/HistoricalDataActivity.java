package tw.brad.apps.cloudfitness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.brad.apps.cloudfitness.java_class.MyExpandableListAdapter;

public class HistoricalDataActivity extends AppCompatActivity {
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String[]>> listDataChild;
    private final String[] mMonths =
            new String[] {"November, 2017", "December, 2017", "January, 2018"};
    //, "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_historical_data);
        init();
    }

    private void init() {
        //TODO
        // 取得list view
        expListView = (ExpandableListView) findViewById(R.id.expand_list);
        // 準備list資料
        prepareListData();

        // 設置調變器
        listAdapter = new MyExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }

    // 配置資料
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // 設置子項目header : 月份
        for (String mMonth : mMonths) {
            listDataHeader.add(mMonth);
            //Log.d("ed43", mMonth);
        }

        // 測試資料 : 11月
        List<String[]> nov = new ArrayList<>();

        String[] nov1 = new String[]{
                "11/11/2017", "Weight 200 lb, BMI 21, Body Fat 36.4%",
                "Muscle Mass 100 lb, Body Water 57.5%"
        };
        String[] nov2 = new String[]{
                "11/14/2017", "Weight 203 lb, BMI 22, Body Fat 38.4%",
                "Muscle Mass 101 lb, Body Water 57.9%"
        };
        String[] nov3 = new String[]{
                "11/23/2017", "Weight 208 lb, BMI 23, Body Fat 39.0%",
                "Muscle Mass 103 lb, Body Water 58.5%"
        };
        nov.add(nov1);nov.add(nov2);nov.add(nov3);

        // 測試資料 : 12月
        List<String[]> dec = new ArrayList<>();
        String[] dec1 = new String[]{
                "12/12/2017", "Weight 203 lb, BMI 21, Body Fat 36.4%",
                "Muscle Mass 100 lb, Body Water 57.5%"
        };
        String[] dec2 = new String[]{
                "12/15/2017", "Weight 206 lb, BMI 22, Body Fat 38.4%",
                "Muscle Mass 101 lb, Body Water 57.9%"
        };
        String[] dec3 = new String[]{
                "12/30/2017", "Weight 208 lb, BMI 23, Body Fat 39.0%",
                "Muscle Mass 103 lb, Body Water 58.5%"
        };
        String[] dec4 = new String[]{
                "12/31/2017", "Weight 205 lb, BMI 23, Body Fat 39.0%",
                "Muscle Mass 103 lb, Body Water 58.5%"
        };
        dec.add(dec1);dec.add(dec2);dec.add(dec3);dec.add(dec4);

        // 測試資料 : 1月
        List<String[]> jan = new ArrayList<>();
        String[] jan1 = new String[]{
                "1/1/2018", "Weight 203 lb, BMI 21, Body Fat 36.4%",
                "Muscle Mass 100 lb, Body Water 57.5%"
        };
        String[] jan2 = new String[]{
                "1/6/2018", "Weight 206 lb, BMI 22, Body Fat 38.4%",
                "Muscle Mass 100 lb, Body Water 57.5%"
        };
        jan.add(jan1);jan.add(jan2);

        listDataChild.put(listDataHeader.get(0), nov);
        listDataChild.put(listDataHeader.get(1), dec);
        listDataChild.put(listDataHeader.get(2), jan);
        //Log.d("ed43", new Gson().toJson(listDataChild));
    }

    //按鈕 : 返回鍵
    public void back(View view){
        finish();
    }
}
