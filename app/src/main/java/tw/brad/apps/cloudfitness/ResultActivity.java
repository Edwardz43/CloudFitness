package tw.brad.apps.cloudfitness;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.inuker.bluetooth.library.search.SearchResult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tw.brad.apps.cloudfitness.java_class.Algorithm;
import tw.brad.apps.cloudfitness.java_class.BLEService;
import tw.brad.apps.cloudfitness.java_class.MyBroadcastReceiver;
import tw.brad.apps.cloudfitness.java_class.data.MyDBHelper;
import tw.brad.apps.cloudfitness.java_class.data.Record;
import tw.brad.apps.cloudfitness.java_class.data.User;

public class ResultActivity extends AppCompatActivity {
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private boolean isDialogShow, isDataUpdated;
    private List<SearchResult> device_List;
    private AlertDialog.Builder dialog_list;
    private String deviceMAC, unit_type;
    private TextView bmi, fat, water, muscle, bone, v_fat, weight, weight_unit;
    // 物件 : 使用者
    private User user;
    // 物件 : 測量記錄
    private Record record;
    // 自訂監聽器 : 彈出對話框用
    private MyOnclickListener mOnclickListener;
    // 自訂廣播接收器
    private MyBroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        setContentView(R.layout.activity_result);
        init();
    }

    // 初始化
    private void init() {
        // 初始化 DB
        dbHelper = new MyDBHelper(this, MyDBHelper.dbN_ame, null, 1);
        db = dbHelper.getReadableDatabase();
        // 從intent中取出User物件
        user = (User) getIntent().getSerializableExtra("user");
        if(user == null){
            finish();
        }
        // 對話框顯示 : 否
        isDialogShow = false;

        // 資料更新 : 否
        isDataUpdated = false;

        // 搜索到的設備列表
        device_List = new ArrayList();

        // 對話框
        dialog_list = new AlertDialog.Builder(this);

        // 自訂監聽器 : 監聽使用者在對話框上所選的選項
        mOnclickListener = new MyOnclickListener();
        mOnclickListener.setParent(this);

        // TextView 初始化
        bmi = findViewById(R.id.bmi);
        fat = findViewById(R.id.body_fat);
        water = findViewById(R.id.body_water);
        muscle = findViewById(R.id.muscle_mass);
        bone = findViewById(R.id.bone_mass);
        v_fat = findViewById(R.id.v_fat);
        weight = findViewById(R.id.weight);
        weight_unit = findViewById(R.id.weight_unit);
        weight.setText("0.0");
        fat.setText("");
        water.setText("");
        muscle.setText("");
        bone.setText("");
        v_fat.setText("");
        bmi.setText("");

        // 初始化體重顯示單位 : 根據使用者偏好
        int option = user.getUnit_type();
        if(option == 0){
            this.unit_type = "lb";
        }else if(option == 1){
            this.unit_type = "kg";
        }

        weight_unit.setText(unit_type);

        // 過濾器 : 讓接收器監聽特定的訊息
        IntentFilter filter = new IntentFilter("BleService");
        receiver = new MyBroadcastReceiver(this);
        registerReceiver(receiver, filter);
        startBLEService();
    }

    private void startBLEService() {
        Log.d("ed43","startBLEService()");
        Intent intent = new Intent(this, BLEService.class);
        intent.putExtra("cmd", 0);
        startService(intent);
    }

    // 按下save按鍵 紀錄測量資料  並返回到 LastWeightActivity
    public void save(View view){
        // 如果Record物件非空值 就寫入資料庫並離開
        if(record != null){
            boolean b = record.insert(db);
            Log.d("ed43", "save : " + b);
            finish();
        }
    }

    // 按下discard按鍵 紀錄測量資料  並返回到 LastWeightActivity
    public void discard(View view){
        Log.d("ed43", "discord");
        finish();
    }

    // 搜索 : 當廣播接收器收到藍芽已開啟 便會啟動搜索
    public void search(){
        Log.d("ed43","search()");
        Intent intent = new Intent(this, BLEService.class);
        intent.putExtra("cmd", 0);
        startService(intent);
    }

    // 跳轉 : 搜索失敗頁面
    public void device_not_found(){
        Log.d("ed43","device_not_found()");
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("condition", "deviceNotFound");
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    // 跳轉 : 連結失敗頁面
    public void connection_lost() {
        Log.d("ed43","connection_lost()");
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("condition", "connectionLost");
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }


    // 連接設備 : 將使用者點選的設備 傳到service 啟動連結
    private void connect(){
        Intent serviceIntent = new Intent(this, BLEService.class);
        serviceIntent.putExtra("cmd",1);
        serviceIntent.putExtra("id", deviceMAC);
        serviceIntent.putExtra("profile", setProfile());
        startService(serviceIntent);
    }

    // 與設備斷開連結
    private void disConnect(){
        Intent serviceIntent = new Intent(this, BLEService.class);
        serviceIntent.putExtra("cmd",2);
        startService(serviceIntent);
    }

    // 吐司對話框 : 引導使用者操作
    public void alertMessage(int option){
        if(option == 0){
            Toast.makeText(this, R.string.stand_on_scale, Toast.LENGTH_LONG).show();
        }else if(option == 1){
            Toast.makeText(this, R.string.leave_scale, Toast.LENGTH_LONG).show();
        }
    }

    // 選擇設備 : 會收到服務傳來的設備List
    public void selectDevice(SearchResult device){
        //Log.d("ed43","deviceSearched : "+device.getName());
        // 對話框顯示 : 否
        isDialogShow = false;
        // 設備列表 初始化
        device_List = new ArrayList<>();

        // 將不重複的設備加進列表裡(因為服務會持續的傳送資料過來 會有重複的設備)
        if(!device_List.contains(device)){
            device_List.add(device);
        }

        // 設置選擇對話框
        dialog_list.setTitle("Select Device");
        String[] device_Name = new String[device_List.size()];
        String[] device_Address = new String[device_List.size()];
        for (int i = 0; i < device_List.size(); i++){
            // 設置藍芽設備的 顯示名稱列表(外顯值)
            device_Name[i] = device_List.get(i).getName();
            // 設置藍芽設備的 MAC列表(內存值)
            device_Address[i] = device_List.get(i).getAddress();
        }
        // 對話框設置屬性 : 單選
        dialog_list.setSingleChoiceItems(device_Name, -1, mOnclickListener);

        // 對話框設置屬性 : OK 鍵
        dialog_list.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log.d("ed43", "select device : " + deviceMAC);
                //使用者若有點選  按下OK鍵後就會啟動連結功能
                connect();

                // 對話框消失
                isDialogShow = false;
            }
        });

        // 取消 : 按下返回鍵
        dialog_list.setCancelable(false);

        // 如果對話框沒有顯示 就 show() 避免重複
        if(!isDialogShow) {
            dialog_list.show();
            isDialogShow = true;
        }

    }

    // 設置使用者個人資料 將資料改成byte[]回傳
    private byte[] setProfile(){
        try {
            // 年齡 : 動態計算 由使用者登入的生日計算
            byte age = (byte) Algorithm.getAge(user.getBirthdate());
            // 性別
            byte sex = (byte)(user.getGender().equals("female")? 0 : 1);
            // 偏好單位 1: 英制  2:公制
            byte unit_type = (byte)(user.getUnit_type() + 1);
            // 身高 : 一律用公分
            byte height = (byte)Integer.parseInt(user.getHeight_cm());
            // 顯示體重單位
            byte weight_unit = (byte)(unit_type == 1? -64: -128);

            // 存進 byte[]
            byte[] res = new byte[]{
                    90, -43, 5, age, sex, unit_type, height,
                    0, weight_unit, 0, 0, 0, 0,-86
            };

            // 計算第12碼檢查碼
            int checkSum = 0;
            for(int i = 0; i < 11; i++){
                checkSum ^= res[i];
            }
            res[12] = (byte)checkSum;
            //回傳結果
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 設置測量結果 : 由服務端接收到資料
    public void setResult(double[] data){
        //Log.d("ed43", new Gson().toJson(data));
        //將收到的資料寫成一筆紀錄
        record = new Record(new Date().getTime(), data, user.getId());
        //如果結果已經更新 就避免重複
        if(!isDataUpdated){
            if(user.getUnit_type() == 0){
                // 因為體脂計只會回傳公斤 若使用者的偏好為英制 就多一個轉換動作
                weight.setText("" + Algorithm.kgToPound(data[0]));
                muscle.setText("" + Algorithm.kgToPound(data[3]) + unit_type);
                bone.setText("" + Algorithm.kgToPound(data[4]) + unit_type);
            }else if(user.getUnit_type() == 1){
                weight.setText("" + data[0]);
                muscle.setText("" + data[3] + unit_type);
                bone.setText("" + data[4] + unit_type);
            }
            isDataUpdated = true;
            fat.setText("" + data[1]+"%");
            water.setText("" + data[2]+"%");
            v_fat.setText("" + (int)data[5]);
            bmi.setText("" + data[6]);
            alertMessage(1);
        }
    }

    // 自訂監聽器 : 對話框點擊監聽器
    private class MyOnclickListener implements DialogInterface.OnClickListener{
        private ResultActivity activity;

        public void setParent(ResultActivity activity){this.activity = activity;}
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // 取得使用者在對話框上點選的藍芽設備的MAC(內存值)
            activity.deviceMAC = activity.device_List.get(i).getAddress();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        //Log.d("ed43", "Result : onDestroy");
        super.onDestroy();
        unregisterReceiver(receiver);
//        disConnect();
//        Intent intent = new Intent(this, BLEService.class);
//        stopService(intent);
    }
}
