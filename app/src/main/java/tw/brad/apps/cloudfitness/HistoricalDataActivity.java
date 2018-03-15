package tw.brad.apps.cloudfitness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HistoricalDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_historical_data);
        init();
    }

    private void init() {
        //TODO
    }

    //按鈕 : 返回鍵
    public void back(View view){
        finish();
    }
}
