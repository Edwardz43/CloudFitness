package tw.brad.apps.cloudfitness;

import android.content.Intent;
import android.content.res.Resources;
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

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private Button male, female, imperial, metric;
    private String email, password, confirmPassword, firstname, lastName,
            birthdate, gender, height_in, height_ft, height_cm, weight_lb, weight_kg;
    private Integer unit_type, activity_level;
    private final Integer IMPERIAL = 0;
    private final Integer METRIC = 1;
    private final String MALE = "male";
    private final String FEMALE = "female";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        init();
    }

    //初始化
    private void init() {
        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();

        //預設性別 : 男性
        this.gender = MALE;
        setGender();

        //預設單位 : 英制
        this.unit_type = IMPERIAL;
        setUnit();

        //初始化下拉選單
        init_spinner();
    }

    // 設置性別按鈕 : 男性
    public void setMale(View view){
        this.gender = MALE;
        setGender();
    }

    // 設置性別按鈕 : 女性
    public void setFemale(View view){
        this.gender = FEMALE;
        setGender();
    }

    // 將性別按鈕的結果記錄下來 被選的按鈕會變色
    private void setGender(){
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        if(this.gender.equals(MALE)){
            // 男性按鈕變深  女性按鈕變淺
            male.setBackgroundResource(R.color.colorSelectedButton);
            female.setBackgroundResource(R.color.colorUnselectedButton);
        }else if (this.gender.equals(FEMALE)){
            // 男性按鈕變淺  女性按鈕變深
            female.setBackgroundResource(R.color.colorSelectedButton);
            male.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    // 設定單位按鈕 : 英制
    public void setImperialUnit(View view){
        //Log.d("ed43", "setImperialUnit");
        this.unit_type = IMPERIAL;
        setUnit();
    }

    // 設定單位按鈕 : 公制
    public void setMetricUnit(View view){
        //Log.d("ed43", "setMetricUnit");
        this.unit_type = METRIC;
        setUnit();
    }

    // 設置單位 : 英制/公制 英制的欄位有 呎 吋 體重  公制的只有 公分 公斤
    private void setUnit(){
        //Log.d("ed43", "setUnit()");
        imperial = findViewById(R.id.imperial_btn);
        metric = findViewById(R.id.metric_btn);
        // 先取得英制/公制欄位的layout
        LinearLayout unitImperialLayout = findViewById(R.id.imperialUnitLayout);
        LinearLayout unitMetricLayout = findViewById(R.id.metricUnitLayout);

        // 切換顯示欄位  被選到的會 VISIBLE  沒被選到的會 GONE
        if(this.unit_type == IMPERIAL){
            //Log.d("ed43", "IMPERIAL");
            unitImperialLayout.setVisibility(View.VISIBLE);
            unitMetricLayout.setVisibility(View.GONE);
            imperial.setBackgroundResource(R.color.colorSelectedButton);
            metric.setBackgroundResource(R.color.colorUnselectedButton);
        }else if(this.unit_type == METRIC){
            //Log.d("ed43", "METRIC");
            unitImperialLayout.setVisibility(View.GONE);
            unitMetricLayout.setVisibility(View.VISIBLE);
            metric.setBackgroundResource(R.color.colorSelectedButton);
            imperial.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    // 按下 back按鍵 回到首頁
    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 按下register按鍵  寫入新的使用者資料 並返回首頁
    public void register(View view){
        this.email = editableToString(findViewById(R.id.user_email));
        this.firstname = editableToString(findViewById(R.id.user_firstName));
        this.lastName = editableToString(findViewById(R.id.user_lastName));
        this.password = editableToString(findViewById(R.id.user_password));
        this.confirmPassword = editableToString(findViewById(R.id.confirm_password));
        this.birthdate = editableToString(findViewById(R.id.user_birthDate));

        // 身高公制/英制顯示的轉換
        // 英制
        if(this.unit_type == IMPERIAL){
            this.height_in = editableToString(findViewById(R.id.height_in));
            this.height_ft = editableToString(findViewById(R.id.height_ft));
            this.height_cm = Algorithm.imperialToMetric(height_ft, height_in);
            // 公制
        }else if(this.unit_type == METRIC){
            this.height_cm = editableToString(findViewById(R.id.height_cm));
            String[] result = Algorithm.metricToImperial(this.height_cm);
            this.height_ft = result[0];
            this.height_in = result[1];
        }

        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setFisrtname(this.firstname);
        user.setLastname(this.lastName);
        user.setBirthdate(this.birthdate);
        user.setGender(this.gender);
        user.setHeight_ft(this.height_ft);
        user.setHeight_in(this.height_in);
        user.setHeight_cm(this.height_cm);
        user.setWeight_lb(this.weight_lb);
        user.setWeight_kg(this.weight_kg);
        user.setUnit_type(this.unit_type);
        user.setActivity_level(this.activity_level);
        user.insert(db);
        //Log.i("DBTest", "Register : "+new Gson().toJson(user));
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    // 下拉式選單(spinner)初始化
    private void init_spinner(){
        Spinner spinner = findViewById(R.id.activity_level);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setPrompt("Activity Level");
        spinner.setOnItemSelectedListener(this);
    }

    // 下拉式選單 : 當下拉項目被點選
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        if (position > 0) {
            // 記錄下使用者的選擇
            this.activity_level = position;
        }
    }

    // 下拉式選單 : 沒有點選任何項目
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // 甚麼都不做
    }

    // 正規化 驗證email
    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    // 取得EditText的值
    private String editableToString(View view){
        return ((EditText)view).getText().toString();
    }
}
