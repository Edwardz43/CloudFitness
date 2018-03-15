package tw.brad.apps.cloudfitness;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    // 使用者偏好 SharedPreferences
    private SharedPreferences sharedPref;
    // 紀錄時間 與離開app相關
    private long last = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // 請求權限
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d("ed43","no permission");
            ActivityCompat.requestPermissions(this,
                    new String[] {
                    // 如果缺少權限  就先動態向使用者請求
                            // 權限 : 取得模糊位置
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            // 權限 : 取得精確位置
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },1 );
        }else {
            // 如果有權限  就開始APP初始化
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 取權限的callback
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // 如果取得正確的權限  才初始化
            Log.d("ed43", "ok");
            init();
        }else {
            // 權限取得失敗
            Log.d("ed43", "xx");
            finish();
        }
    }

    // 初始化
    private void init() {
        // 取得 sharedPreferences
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // 從 sharedPreferences 中取得 reMemberMe 的值  如果沒有 就預設 false
        boolean reMemberMe = sharedPref.getBoolean("rememberMe", false);
        if(reMemberMe){
            Log.d("ed43", "remember me");
        }
    }

    //登入
    public void signIn(View view){
        Intent signInIntent = new Intent(this, LastWeightActivity.class);
        startActivity(signInIntent);
    }

    //註冊
    public void register(View view){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    //記住我
    public void rememberMe(View view){
        CheckBox rememberCheckBox = (CheckBox) view;
        boolean checked = rememberCheckBox.isChecked();
        SharedPreferences.Editor editor = sharedPref.edit();

        // 將checkbox  結果記錄在SharedPreferences
        editor.putBoolean("rememberMe",checked);
        editor.commit();

        Log.i("ed43", "IsChecked : " + checked);
    }

    //fb登入
    public void fb_login(View view){
        Log.i("ed43", "fb_login");
    }

    @Override
    public void onDestroy() {
        Log.d("ed43", "Destroy");
        super.onDestroy();
        // 將app的程序清除掉
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    // 改寫按下返回鍵 離開app
    @Override
    public void finish() {
        //第一次按下返回鍵 會先觸發一次finish() 取得按下返回鍵的時間
        long now = System.currentTimeMillis();

        // 兩次按下返回鍵的間隔時間 < 3秒  就離開app
        if(now - last <= 3 * 1000){
            super.finish();
        }else {
            // 第一次按下返回鍵  now - last 一定會 > 3 * 1000
            last = now;
            Toast.makeText(this,"Click Back one more to exit",Toast.LENGTH_SHORT).show();
        }

    }

    //忘記密碼 會開啟cloudfitness的網頁超連結
    public void forgot_password(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://mycloudfitness.com/forgetpassword/"));
        startActivity(intent);
    }
}
