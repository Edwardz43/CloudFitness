package tw.brad.apps.cloudfitness;

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

import com.google.gson.Gson;
import com.inuker.bluetooth.library.search.SearchResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private boolean isDialogShow, isDataUpdated;
    private List<SearchResult> device_List;
    private AlertDialog.Builder dialog_list;
    private String deviceMAC, unit_type;
    private TextView bmi, fat, water, muscle, bone, v_fat, weight, weight_unit;


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
        // 對話框顯示 : 否
        isDialogShow = false;

        // 資料更新 : 否
        isDataUpdated = false;

        // 搜索到的設備列表
        device_List = new ArrayList();

        // 對話框
        dialog_list = new AlertDialog.Builder(this);


        // TextView 初始化
        bmi = findViewById(R.id.bmi);
        fat = findViewById(R.id.body_fat);
        water = findViewById(R.id.body_water);
        muscle = findViewById(R.id.muscle_mass);
        bone = findViewById(R.id.bone_mass);
        v_fat = findViewById(R.id.v_fat);
        weight = findViewById(R.id.weight);
        weight_unit = findViewById(R.id.weight_unit);
        weight.setText("000.0");
        fat.setText("");
        water.setText("");
        muscle.setText("");
        bone.setText("");
        v_fat.setText("");
        bmi.setText("");
        this.unit_type = "lb";
        weight_unit.setText(unit_type);
    }

    // 按下save按鍵 紀錄測量資料  並返回到 LastWeightActivity
    public void save(View view){
        Log.d("ed43", "save");
        finish();
    }

    // 按下discard按鍵 紀錄測量資料  並返回到 LastWeightActivity
    public void discard(View view){
        Log.d("ed43", "discord");
        finish();
    }
}
