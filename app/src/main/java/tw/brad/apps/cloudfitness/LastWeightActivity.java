package tw.brad.apps.cloudfitness;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import java.util.List;

import tw.brad.apps.cloudfitness.java_class.data.MyDBHelper;
import tw.brad.apps.cloudfitness.java_class.data.Record;
import tw.brad.apps.cloudfitness.java_class.data.User;

public class LastWeightActivity extends AppCompatActivity {
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    // 使用者物件
    private User user;
    // 物件 : 測量記錄
    private Record record;
    private List<Record> records;
    // 紀錄時間 與離開app相關
    private long last = 0;
    private TextView last_weight, last_weight_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_last_weight);
        onResume();
    }

    // 初始化
    private void init() {
        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();

        // 初始化 TV
        last_weight = findViewById(R.id.last_weight);
        last_weight_unit = findViewById(R.id.last_weight_unit);

        // 從intent中取出Email 到資料庫搜索
        String email = getIntent().getStringExtra("email");
        if(email != null){
            user = User.query(email, db);
        }else {
            // 從intent中取出User物件
            user = (User)getIntent().getSerializableExtra("user");
        }

        //Log.d("ed43", "LastWeight : user " + new Gson().toJson(user));
        if(user == null){
            //若找不到使用者 就退回首頁
            finish();
        }
        // 從資料庫中搜索測量記錄
        records = Record.query(user.getId(), db);
        if(records.size() > 0){
            //取最後一筆的體重
            last_weight.setText("" + records.get(records.size() - 1).getWeight());
        }
        last_weight_unit.setText((user.getUnit_type() == 0 ? "lb":"kg"));
    }

    // 跳頁 : myProfile
    public void myProfile(View view){
        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    // 跳頁 : 測量體重
    public void scale(View view){
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // 跳頁 : 圖表顯示
    public void graph(View view){
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // 跳頁 : 歷史紀錄
    public void hist(View view){
        Intent intent = new Intent(this, HistoricalDataActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // 跳頁 : 登出
    public void signOut(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 改寫返回鍵 : 執行finish() 會回到首頁
    @Override
    public void onBackPressed() {
        //第一次按下返回鍵 會先觸發一次finish() 取得按下返回鍵的時間
        long now = System.currentTimeMillis();

        // 兩次按下返回鍵的間隔時間 < 3秒  就離開app
        if(now - last <= 3 * 1000){
            signOut(null);
        }else {
            // 第一次按下返回鍵  now - last 一定會 > 3 * 1000
            last = now;
            Toast.makeText(this,"Click Back one more to sign out",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        //Log.d("ed43", "LastWeight : omResume");
        super.onResume();
        init();
    }
}
