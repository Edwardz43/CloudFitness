package tw.brad.apps.cloudfitness;

import android.content.Intent;
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

import tw.brad.apps.cloudfitness.java_class.Algorithm;
import tw.brad.apps.cloudfitness.java_class.data.MyDBHelper;
import tw.brad.apps.cloudfitness.java_class.data.User;

public class MyProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private boolean isFBNewLogin, isMale, isImperial;
    private Button male, female, imperial, metric;
    private EditText email, firstname, lastname, birthdate, height_ft, height_in, height_cm;
    private Integer activity_level;
    private Spinner spinner;
    private final static int IMPERIAL = 0;
    private final static int METRIC = 1;
    private final String MALE = "male";
    private final String FEMALE = "female";
    // 使用者物件
    private User user;
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
        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();

        // 從intent中取出User物件
        user = (User)getIntent().getSerializableExtra("user");
        //Log.d("ed43", new Gson().toJson(user));
        if(user == null){
            //若找不到使用者 就退回首頁
            finish();
        }

        // 初始化所有的 EditText
        email = findViewById(R.id.profile_email);
        firstname = findViewById(R.id.profile_firstName);
        lastname = findViewById(R.id.profile_lastName);
        birthdate = findViewById(R.id.profile_birthDate);
        height_ft = findViewById(R.id.profile_height_ft);
        height_in = findViewById(R.id.profile_height_in);
        height_cm = findViewById(R.id.profile_height_cm);

        // 判斷是否為FB登入
        if(user.getEmail() == null){
            isFBNewLogin = true;
            isImperial = true;
            isMale = true;
        }else {
            isFBNewLogin = false;
        }
        setMyProfile();

        //初始化下拉選單
        init_spinner();
    }

    private void setMyProfile(){
        if(!isFBNewLogin){
            email.setText(user.getEmail());
            firstname.setText(user.getFisrtname());
            lastname.setText(user.getLastname());
            birthdate.setText(user.getBirthdate());
            height_ft.setText(user.getHeight_ft());
            height_in.setText(user.getHeight_in());
            height_cm.setText(user.getHeight_cm());
            isMale = user.getGender().equals(MALE);
            isImperial = (user.getUnit_type() == IMPERIAL);
        }
        // 設置性別
        setGender();
        // 設置偏好單位
        setUnit();

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
            user.setGender(MALE);
            male.setBackgroundResource(R.color.colorSelectedButton);
            female.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            // 男性按鈕變淺  女性按鈕變深
            user.setGender(FEMALE);
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
            //Log.d("ed43", "setUnit() : IMPERIAL");
            user.setUnit_type(IMPERIAL);
            unitImperialLayout.setVisibility(View.VISIBLE);
            unitMetricLayout.setVisibility(View.GONE);
            imperial.setBackgroundResource(R.color.colorSelectedButton);
            metric.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            //Log.d("ed43", "setUnit() : METRIC");
            user.setUnit_type(METRIC);
            unitImperialLayout.setVisibility(View.GONE);
            unitMetricLayout.setVisibility(View.VISIBLE);
            metric.setBackgroundResource(R.color.colorSelectedButton);
            imperial.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    // 按下 back按鍵 回到LastWeight 傳送User未更動的原始資料
    public void back(View view){
        Intent intent = new Intent(this, LastWeightActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    // 按下save按鍵  更新使用者資料 並返回首頁
    public void save(View view){
        user.setEmail(this.email.getText().toString());
        user.setFisrtname(this.firstname.getText().toString());
        user.setLastname(this.lastname.getText().toString());
        user.setBirthdate(this.birthdate.getText().toString());
        user.setActivity_level(this.activity_level);

        if(user.getUnit_type() == IMPERIAL){
            user.setHeight_ft(this.height_ft.getText().toString());
            user.setHeight_in(this.height_in.getText().toString());

            user.setHeight_cm(Algorithm.imperialToMetric(user.getHeight_ft(), user.getHeight_in()));

        }else if(user.getUnit_type() == METRIC){
            user.setHeight_cm(height_cm.getText().toString());
            String[] result = Algorithm.metricToImperial(user.getHeight_cm());
            user.setHeight_ft(result[0]);
            user.setHeight_in(result[1]);
        }

        if(isFBNewLogin){
            boolean b = user.insert(db);
            //Log.d("ed43", "insert : " + b);
        }else {

            boolean b = user.update(db);
            //Log.d("ed43", "update : " + b);
        }
        Intent intent = new Intent(this, LastWeightActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
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
        spinner.setSelection(user.getActivity_level());
    }

    // 下拉式選單 : 當下拉項目被點選
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        //Log.i("ed43", "spinner select item : " + position);
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

    // 改寫返回鍵 : 執行back()
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back(null);
    }

    // 驗證 email 格式
    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        // 回傳布林值 : 是否符合格式
        return matcher.find();
    }
}
