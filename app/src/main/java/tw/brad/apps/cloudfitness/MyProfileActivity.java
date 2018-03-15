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
    private Resources res;
    private boolean isFBNewLogin;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_my_profile);
        init();
    }

    private void init() {
        isFBNewLogin = false;
        res = getResources();
        email = findViewById(R.id.profile_email);
        firstname = findViewById(R.id.profile_firstName);
        lastname = findViewById(R.id.profile_lastName);
        birthdate = findViewById(R.id.profile_birthDate);
        height_ft = findViewById(R.id.profile_height_ft);
        height_in = findViewById(R.id.profile_height_in);
        height_cm = findViewById(R.id.profile_height_cm);


        init_spinner();
    }

    public void setMale(View view){
        isMale = true;
        setSex();
    }

    public void setFemale(View view){
        isMale = false;
        setSex();
    }

    private void setSex(){
        male = findViewById(R.id.profile_male);
        female = findViewById(R.id.profile_female);
        if(isMale){
            male.setBackgroundResource(R.color.colorSelectedButton);
            female.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            female.setBackgroundResource(R.color.colorSelectedButton);
            male.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    public void setImperialUnit(View view){
        isImperial = true;
        setUnit();
    }

    public void setMetricUnit(View view){
        isImperial = false;
        setUnit();
    }

    private void setUnit(){
        imperial = findViewById(R.id.profile_imperial_btn);
        metric = findViewById(R.id.profile_metric_btn);
        LinearLayout unitImperialLayout = findViewById(R.id.imperialUnitLayout);
        LinearLayout unitMetricLayout = findViewById(R.id.metricUnitLayout);
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

    public void back(View view){
        MyProfileActivity.this.finish();
    }

    public void save(View view){
        MyProfileActivity.this.finish();
    }

    private void init_spinner(){
        spinner = findViewById(R.id.profile_activity_level);
        String[] optiopns = {
                "Activity Level",
                "Low (0-1 workouts/week)",
                "Medium (2-4 workouts/week)",
                "High (>5 workouts/week)"
        };
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item, optiopns){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

//            @Override
//            public View getDropDownView(
//                    int position, View convertView, ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//                if(position == 0){
//                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY);
//                }
//                else {
//                    tv.setTextColor(Color.BLACK);
//                }
//                return view;
//            }
        };
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        Log.i("ble_weight_scale", "spinner select item : " + position);
        String selectedItemText = (String) parent.getItemAtPosition(position);
        // If user change the default selection
        // First item is disable and it is used for hint
        if (position > 0) {
            // Notify the selected item text
            this.activity_level = position;
            //Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //驗證email by regex
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
