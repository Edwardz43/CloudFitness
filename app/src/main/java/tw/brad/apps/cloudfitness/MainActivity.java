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
    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    private Timer timerExit;
    private TimerTask task;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_main);

        // request Permission
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d("ed43","no permission");
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },1 );
        }else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d("ed43", "ok");
            init();
        }else {
            Log.d("ed43", "xx");
            finish();
        }
    }

    private void init() {

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean reMemberMe = sharedPref.getBoolean("rememberMe", false);
        if(reMemberMe){
            Log.d("ed43", "remember me");
        }
        //init

        //testInsert();
        //init exit app
        timerExit = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                isExit = false;
                hasTask = true;
            }
        };

    }


    //登入
    public void signIn(View view){
        //Log.i("ed43", "signIn");
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
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    //離開app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 判斷是否按下Back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否要退出
            if(isExit == false ) {
                isExit = true;
                Log.d("ed43", "Stay");
                Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
                if(!hasTask) {
                    this.timerExit.schedule(task, 2000);
                }
            } else {
                Log.d("ed43", "Leave");
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
            }
        }
        return false;
    }

    //忘記密碼
    public void forgot_password(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://mycloudfitness.com/forgetpassword/"));
        startActivity(intent);
    }
}
