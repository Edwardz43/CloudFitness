package tw.brad.apps.cloudfitness;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;

import tw.brad.apps.cloudfitness.java_class.BLEService;
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
    // 判斷GPS是否開啟
    private boolean isGpsOPen;
    // 藍芽支援功能相關物件
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    // FB登入相關物件
    private CallbackManager callbackManager;
    private Button fbLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private FacebookCallback mFBCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // 取得手機API版本 若是5版 就進行藍芽功能檢查
        int sdk_version =  Integer.valueOf(Build.VERSION.SDK_INT);
        if(sdk_version == 21 || sdk_version == 22){
            bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

            // 檢查5版的藍芽支援 : 沒過會跳出訊息  然後離開app
            if(!checkBluetooth()){
                final MainActivity activity = this;
                AlertDialog.Builder builder =  new AlertDialog.Builder(this);
                String message = "Your device do not support BLE";
                builder.setMessage(message).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                showGPSOpenMessage(true);
                                d.dismiss();
                                activity.finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }

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
            //Log.d("ed43", "onRequestPermissionsResult");
            init();
        }else {
            // 權限取得失敗
            //Log.d("ed43", "xx");
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 初始化
    private void init() {
        //Log.d("ed43", "init()");
        //FB登入
        callbackManager = CallbackManager.Factory.create();

        fbLoginButton = (Button) findViewById(R.id.login_button);

        fbLoginButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = loginResult.getAccessToken();

                Log.d("ed43","access token got.");

                //send request and call graph api

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名 ID FB個人頁面連結
                                String fb_id = object.optString("id");
                                signInWithFb(fb_id);
                                Log.d("FB Test", "FB ID : " + fb_id);
                            }
                        });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
                //Log.i("FB Test","loginResult : " + loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("ed43","CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("ed43",exception.toString());
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();
        // 取得 sharedPreferences
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // 從 sharedPreferences 中取得 reMemberMe 的值  如果沒有 就預設 false
        boolean reMemberMe = sharedPref.getBoolean("rememberMe", false);
        if(reMemberMe){
            //Log.d("ed43", "remember me");
        }
    }

    // for 5.0(API 21)檢查藍芽功能是否支援
    @TargetApi(21)
    private boolean checkBluetooth(){
        return
                bluetoothAdapter.isMultipleAdvertisementSupported() &&
                bluetoothAdapter.isOffloadedFilteringSupported() &&
                bluetoothAdapter.isOffloadedScanBatchingSupported();
    }

    //登入
    public void signIn(View view){
        EditText login_email = (EditText) findViewById(R.id.login_email);
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
        }
        /*Intent signInIntent = new Intent(this, LastWeightActivity.class);
        User sampleUser = User.getSampleUser(db);
        signInIntent.putExtra("user", sampleUser);
        Log.i("ed43", "user : " + new Gson().toJson(sampleUser));
        startActivity(signInIntent);
        finish();*/
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
        SharedPreferences.Editor editor = sharedPref.edit();

        // 將checkbox  結果記錄在SharedPreferences
        editor.putBoolean("rememberMe",checked);
        editor.commit();

        //Log.i("ed43", "IsChecked : " + checked);
    }

    //fb登入
    public void fb_login(View view){
        LoginManager.getInstance().
                logInWithReadPermissions(this, Arrays.asList("public_profile"));
        Log.i("ed43", "fb_login");
    }

    // 由FB登入的callback的跳頁
    private void signInWithFb(String fb_id){
        Intent lastWeightIntent = new Intent(getApplicationContext(), LastWeightActivity.class);
        //用FB ID 到DB搜索 若沒有 就判定為新使用者
        long id = Long.parseLong(fb_id);
        User user = User.query(id, db);
        if(user == null){
            Log.d("ed43", "user null");
            user = new User();
            user.setFb_id(id);
        }
        lastWeightIntent.putExtra("user", user);
        startActivity(lastWeightIntent);
        finish();
    }

    // 改寫按下返回鍵 離開app
    @Override
    public void onBackPressed() {
        //第一次按下返回鍵 會先觸發一次finish() 取得按下返回鍵的時間
        long now = System.currentTimeMillis();

        // 兩次按下返回鍵的間隔時間 < 3秒  就離開app
        if(now - last <= 3 * 1000){
            accessTokenTracker.stopTracking();
            LoginManager.getInstance().logOut();
            Intent serviceIntent = new Intent(this, BLEService.class);
            serviceIntent.putExtra("cmd",2);
            startService(serviceIntent);
            stopService(serviceIntent);
            finish();
        }else {
            // 第一次按下返回鍵  now - last 一定會 > 3 * 1000
            last = now;
            Toast.makeText(this,"Click Back one more to exit",Toast.LENGTH_SHORT).show();
        }

    }

    // 改寫onResume : 每次回到首頁都先檢查GPS是否開啟
    @Override
    public void onResume(){
        //Log.d("ed43", "onResume");
        super.onResume();
        isGpsOPen = gpsStatus(this);

        // 如果GPS未開啟 就打開
        if(!isGpsOPen){
            // 開啟GPS
            openGPS();
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

    // 開啟GPS定位
    public void openGPS() {
        //Log.d("ed43", "openGPS");
        final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        final String message = "Do you want open GPS setting to connect scale?";
        builder.setMessage(message).setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int id) {
                    showGPSOpenMessage(true);
                    d.dismiss();
                }
            })
            .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int id) {
                        d.cancel();
                        showGPSOpenMessage(false);
                }
            }
        );

        Dialog dialog = builder.create();
        // 取消 : 按下他地方
        dialog.setCanceledOnTouchOutside(false);
        // 取消 : 按下返回鍵
        dialog.setCancelable(false);
        dialog.show();
    }

    // 判斷GPS是否開啟 : 若未開啟 將引導使用者置設定頁面
    public static final boolean gpsStatus(final Context context) {
        //Log.d("ed43", "isGpsOPen");
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通過GPS衛星定位，定位級別可以精確到街（通過24顆衛星定位，在室外和空曠的地方定位准確、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通過WLAN或移動網絡(3G/2G)確定的位置（也稱作AGPS，輔助GPS定位。主要用於在室內或遮蓋物（建築群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    // 引導使用者開啟GPS
    private void showGPSOpenMessage(boolean option){
        if(option){
            // 若使用者按下OK 就跳到設置頁面
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            startActivity(new Intent(action));
        }else {
            // 若使用者按下取消 就離開APP
            Toast.makeText(
                    this,
                    "You have to open GPS to connect BLE device.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
