package tw.brad.apps.cloudfitness;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private boolean isMale, isImperial;
    private Button male, female, imperial, metric;
    private EditText email, firstname, lastname, birthdate, height_ft, height_in, height_cm;
    private Integer activity_level;
    private Spinner spinner;
    // 正規化 : 驗證email
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_my_profile);
        init();
    }

    // 初始化 將使用者的基本資料寫入到UI
    private void init() {
        // 初始化所有的 EditText
        email = findViewById(R.id.profile_email);
        firstname = findViewById(R.id.profile_firstName);
        lastname = findViewById(R.id.profile_lastName);
        birthdate = findViewById(R.id.profile_birthDate);
        height_ft = findViewById(R.id.profile_height_ft);
        height_in = findViewById(R.id.profile_height_in);
        height_cm = findViewById(R.id.profile_height_cm);

        //預設性別 : 男性
        isMale = true;
        setGender();
        //預設單位 : 英制
        isImperial = true;
        setUnit();

        //初始化下拉選單
        init_spinner();
    }

    // 設置性別按鈕 : 男性
    public void setMale(View view){
        isMale = true;
        setGender();
    }

    // 設置性別按鈕 : 女性
    public void setFemale(View view){
        isMale = false;
        setGender();
    }

    // 將性別按鈕的結果記錄下來 被選的按鈕會變色
    private void setGender(){
        male = findViewById(R.id.profile_male);
        female = findViewById(R.id.profile_female);
        if(isMale){
            // 男性按鈕變深  女性按鈕變淺
            male.setBackgroundResource(R.color.colorSelectedButton);
            female.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            // 男性按鈕變淺  女性按鈕變深
            female.setBackgroundResource(R.color.colorSelectedButton);
            male.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    // 設定單位按鈕 : 英制
    public void setImperialUnit(View view){
        isImperial = true;
        setUnit();
    }

    // 設定單位按鈕 : 公制
    public void setMetricUnit(View view){
        isImperial = false;
        setUnit();
    }

    // 設置單位 : 英制/公制 英制的欄位有 呎 吋 體重  公制的只有 公分 公斤
    private void setUnit(){
        imperial = findViewById(R.id.profile_imperial_btn);
        metric = findViewById(R.id.profile_metric_btn);
        // 先取得英制/公制欄位的layout
        LinearLayout unitImperialLayout = findViewById(R.id.imperialUnitLayout);
        LinearLayout unitMetricLayout = findViewById(R.id.metricUnitLayout);

        // 切換顯示欄位  被選到的會 VISIBLE  沒被選到的會 GONE
        if(isImperial){
            unitImperialLayout.setVisibility(View.VISIBLE);
            unitMetricLayout.setVisibility(View.GONE);
            imperial.setBackgroundResource(R.color.colorSelectedButton);
            metric.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            unitImperialLayout.setVisibility(View.GONE);
            unitMetricLayout.setVisibility(View.VISIBLE);
            metric.setBackgroundResource(R.color.colorSelectedButton);
            imperial.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    // 按下 back按鍵 回到首頁
    public void back(View view){
        finish();
    }

    // 按下save按鍵  更新使用者資料 並返回首頁
    public void save(View view){
        finish();
    }

    // 下拉式選單(spinner)初始化
    private void init_spinner(){
        spinner = findViewById(R.id.profile_activity_level);
        String[] optiopns = {
                "Activity Level",
                "Low (0-1 workouts/week)",
                "Medium (2-4 workouts/week)",
                "High (>5 workouts/week)"
        };
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item, optiopns){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) {
                    // 將第一個選項設置為不能點選 當作提示選項
                    return false;
                }
                else
                {
                    return true;
                }
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    // 下拉式選單 : 當下拉項目被點選
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        Log.i("ed43", "spinner select item : " + position);
        if (position > 0) {
            // 如果點選的選項不是第一個提示選項  就記錄下使用者的選擇
            this.activity_level = position;
        }
    }

    // 下拉式選單 : 沒有點選任何項目
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // 甚麼都不做
    }

    // 改寫返回鍵 : 執行finish() 會回到首頁
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // 驗證 email 格式
    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        // 回傳布林值 : 是否符合格式
        return matcher.find();
    }
}
