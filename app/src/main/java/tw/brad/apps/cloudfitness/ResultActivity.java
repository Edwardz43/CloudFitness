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
    private double[] forRecordItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_result);
        init();
    }

    private void init() {
        isDialogShow = false;
        isDataUpdated = false;
        device_List = new ArrayList();
        dialog_list = new AlertDialog.Builder(this);


        // init TextView
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

    public void save(View view){
        Log.d("ed43", "save");
        Intent intent = new Intent(this, LastWeightActivity.class);
        startActivity(intent);
    }

    public void discard(View view){
        ResultActivity.this.finish();
    }
}
