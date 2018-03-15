package tw.brad.apps.cloudfitness;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class LastWeightActivity extends AppCompatActivity {
    // 紀錄時間 與離開app相關
    private long last = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_last_weight);
        init();
    }

    // 初始化
    private void init() {
        //TODO
    }

    // 跳頁 : myProfile
    public void myProfile(View view){
        Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
        startActivity(myProfileIntent);
    }

    // 跳頁 : 測量體重
    public void scale(View view){
        Intent scaleIntent = new Intent(this, ResultActivity.class);
        startActivity(scaleIntent);
    }

    // 跳頁 : 圖表顯示
    public void graph(View view){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    // 跳頁 : 歷史紀錄
    public void hist(View view){
        Intent intent = new Intent(this, HistoricalDataActivity.class);
        startActivity(intent);
    }

    // 跳頁 : 登出
    public void signOut(View view){
        finish();
    }

    // 改寫返回鍵 : 執行finish() 會回到首頁
    @Override
    public void onBackPressed() {
        //第一次按下返回鍵 會先觸發一次finish() 取得按下返回鍵的時間
        long now = System.currentTimeMillis();

        // 兩次按下返回鍵的間隔時間 < 3秒  就離開app
        if(now - last <= 3 * 1000){
            super.finish();
        }else {
            // 第一次按下返回鍵  now - last 一定會 > 3 * 1000
            last = now;
            Toast.makeText(this,"Click Back one more to sign out",Toast.LENGTH_SHORT).show();
        }
    }
}
