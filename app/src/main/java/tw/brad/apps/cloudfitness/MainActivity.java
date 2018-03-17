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
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import tw.brad.apps.cloudfitness.java_class.data.MyDBHelper;
import tw.brad.apps.cloudfitness.java_class.data.User;

public class MainActivity extends AppCompatActivity {
    // 使用者偏好 SharedPreferences
    private SharedPreferences sharedPref;
    // 紀錄時間 與離開app相關
    private long last = 0;
    // DB相關物件
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // 請求權限
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Log.d("ed43","no permission");
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
            //Log.d("ed43", "ok");
            init();
        }else {
            // 權限取得失敗
            //Log.d("ed43", "xx");
            finish();
        }
    }

    // 初始化
    private void init() {
        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();
        // 取得 sharedPreferences
        /*sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // 從 sharedPreferences 中取得 reMemberMe 的值  如果沒有 就預設 false
        boolean reMemberMe = sharedPref.getBoolean("rememberMe", false);
        if(reMemberMe){
            //Log.d("ed43", "remember me");
        }*/
    }

    //登入
    public void signIn(View view){
       /* EditText login_email = (EditText) findViewById(R.id.login_email);
        EditText login_password = (EditText) findViewById(R.id.login_password);
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();

        //核對 email & password
        if(email.length() > 0 && password.length() > 0){
            //Log.i("ed43", "confirm user");
            if(confirm_user(email, password)){
                //Log.i("ed43", "Login Email : " + email);
                Intent signInIntent = new Intent(this, LastWeightActivity.class);
                signInIntent.putExtra("email", email);
                startActivity(signInIntent);
                finish();
            }else{
                Toast.makeText(this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
            }
        }else if(email.length() == 0){
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }else if(email.length() > 0 && password.length() == 0){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }*/
        Intent signInIntent = new Intent(this, LastWeightActivity.class);
        User sampleUser = User.getSampleUser(db);
        signInIntent.putExtra("user", sampleUser);
        Log.i("ed43", "user : " + new Gson().toJson(sampleUser));
        startActivity(signInIntent);
        finish();
    }

    //註冊
    public void register(View view){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    //記住我
    public void rememberMe(View view){
        CheckBox rememberCheckBox = (CheckBox) view;
        boolean checked = rememberCheckBox.isChecked();
        /*SharedPreferences.Editor editor = sharedPref.edit();

        // 將checkbox  結果記錄在SharedPreferences
        editor.putBoolean("rememberMe",checked);
        editor.commit();*/

        //Log.i("ed43", "IsChecked : " + checked);
    }

    //fb登入
    public void fb_login(View view){
        Log.i("ed43", "fb_login");
    }

    // 改寫按下返回鍵 離開app
    @Override
    public void onBackPressed() {
        //第一次按下返回鍵 會先觸發一次finish() 取得按下返回鍵的時間
        long now = System.currentTimeMillis();

        // 兩次按下返回鍵的間隔時間 < 3秒  就離開app
        if(now - last <= 3 * 1000){
            finish();
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

    //驗證User
    private boolean confirm_user(String email, String password){
        // 用使用者輸入的帳號密碼到資料庫檢查
        User confirm_user = User.query(email, db);
        if(confirm_user != null){
            String user_password = confirm_user.getPassword();
            if(user_password.equals(password)){
                //Log.d("USER_TEST", "OOOOOOO");
                return true;
            }else {
                //Log.d("USER_TEST", "XXXXXX");
                return false;
            }
        }else {
            ///Log.d("USER_TEST", "GGGGG");
            return false;
        }
    }
}
