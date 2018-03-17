package tw.brad.apps.cloudfitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import tw.brad.apps.cloudfitness.java_class.data.User;

public class ErrorActivity extends AppCompatActivity {
    private User user;
    private String condition;
    private static final String DEVICE_NOT_FOUND = "deviceNotFound";
    private static final String CONNECTION_LOST = "connectionLost";
    private TextView error_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_error);

        init();
    }

    private void init() {
        error_condition = findViewById(R.id.error_condition);
        user = (User) getIntent().getSerializableExtra("user");
        //Log.d("ed43", new Gson().toJson(user));
        condition = getIntent().getStringExtra("condition");
        if(condition.equals(DEVICE_NOT_FOUND)){
            error_condition.setText("DEVICE NOT FOUND");
        }else if(condition.equals(CONNECTION_LOST)){
            error_condition.setText("CONNECTION LOST");
        }
    }

    public void retry(View view){
        Intent it = new Intent(this, ResultActivity.class);
        it.putExtra("user", user);
        startActivity(it);
        finish();
    }

    public void back(View view){
        finish();
    }
}
