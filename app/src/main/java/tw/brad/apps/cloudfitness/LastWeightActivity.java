package tw.brad.apps.cloudfitness;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.login.LoginManager;

public class LastWeightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_last_weight);
        init();
    }

    private void init() {
        //TODO
    }

    public void myProfile(View view){
        Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
        startActivity(myProfileIntent);
    }

    public void scale(View view){
        Intent scaleIntent = new Intent(this, ResultActivity.class);
        startActivity(scaleIntent);
    }

    public void graph(View view){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public void hist(View view){
        Intent intent = new Intent(this, HistoricalDataActivity.class);
        startActivity(intent);
    }

    public void signOut(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        LastWeightActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LastWeightActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
