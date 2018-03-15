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

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private boolean isMale, isImperial;
    private Button male, female, imperial, metric;
    private String email, password, confirmPassword, firstname, lastName,
            birthdate, gender, height_in, height_ft, height_cm, weight_lb, weight_kg;
    private Integer unit_type, activity_level;
    private final Integer IMPERIAL = 0;
    private final Integer METRIC = 0;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_register);
        init();
    }

    //初始化
    private void init() {
        res = getResources();

        //預設性別 : 男性
        isMale = true;
        this.gender = res.getString(R.string.gender_male);
        setSex();

        //預設單位 : 英制
        isImperial = true;
        this.unit_type = res.getInteger(R.integer.IMPERIAL);
        setUnit();

        //初始化下拉選單
        init_spinner();
    }

    // 設置性別按鈕 : 男性
    public void setMale(View view){
        this.gender = "male";
        setSex();
    }

    // 設置性別按鈕 : 女性
    public void setFemale(View view){
        this.gender = "female";
        setSex();
    }

    // 將性別按鈕的結果記錄下來 被選的按鈕會變色
    private void setSex(){
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        if(this.gender.equals("male")){
            // 男性按鈕變深  女性按鈕變淺
            male.setBackgroundResource(R.color.colorSelectedButton);
            female.setBackgroundResource(R.color.colorUnselectedButton);
        }else if (this.gender.equals("female")){
            // 男性按鈕變淺  女性按鈕變深
            female.setBackgroundResource(R.color.colorSelectedButton);
            male.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    // 設定單位按鈕 : 公制
    public void setImperialUnit(View view){
        this.unit_type = IMPERIAL;
        setUnit();
    }

    // 設定單位按鈕 : 公制
    public void setMetricUnit(View view){
        this.unit_type = METRIC;
        setUnit();
    }

    private void setUnit(){
        imperial = findViewById(R.id.imperial_btn);
        metric = findViewById(R.id.metric_btn);
        LinearLayout unitImperialLayout = findViewById(R.id.imperialUnitLayout);
        LinearLayout unitMetricLayout = findViewById(R.id.metricUnitLayout);
        if(this.unit_type == IMPERIAL){
            unitImperialLayout.setVisibility(View.VISIBLE);
            unitMetricLayout.setVisibility(View.GONE);
            imperial.setBackgroundResource(R.color.colorSelectedButton);
            metric.setBackgroundResource(R.color.colorUnselectedButton);
        }else if(this.unit_type == METRIC){
            unitImperialLayout.setVisibility(View.GONE);
            unitMetricLayout.setVisibility(View.VISIBLE);
            metric.setBackgroundResource(R.color.colorSelectedButton);
            imperial.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    public void back(View view){
        finish();
    }

    public void register(View view){
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        finish();
    }

    //下拉式選單
    private void init_spinner(){
        Spinner spinner = findViewById(R.id.activity_level);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setPrompt("Activity Level");
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        if (position > 0) {
            this.activity_level = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //驗證email by regex
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private String editableToString(View view){
        return ((EditText)view).getText().toString();
    }
}
