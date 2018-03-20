package tw.brad.apps.cloudfitness.java_class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.inuker.bluetooth.library.search.SearchResult;

import tw.brad.apps.cloudfitness.ResultActivity;

/**
 * Created by EdLo on 2018/3/15.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private ResultActivity activity;

    public MyBroadcastReceiver(){}

    public MyBroadcastReceiver(ResultActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 接收訊息 : 藍芽開啟
        boolean isBluetoothOpen = intent.getBooleanExtra("isBluetoothOpen",false);
        if(isBluetoothOpen){
            //Log.d("ed43", "BLE:receiver: search()");
            // 啟動 連結
            activity.search();
        }

        // 接收訊息 : 設備已搜索到
        boolean isDeviceSearched = intent.getBooleanExtra("isDeviceSearched",false);
        if(isDeviceSearched){
            // 啟動 選擇連結設備
            SearchResult device = (SearchResult) intent.getParcelableExtra("device");
            //Log.d("ed43", device.getName() + ":" + device.getAddress());
            activity.selectDevice(device);
        }

        // 接收訊息 : 搜索失敗
        boolean deviceNotFound = intent.getBooleanExtra("deviceNotFound",false);
        if(deviceNotFound){
            // 啟動 跳轉搜索失敗
            activity.device_not_found();
        }

        // 接收訊息 : 連結錯誤
        boolean connectionLost = intent.getBooleanExtra("connectionLost",false);
        if(connectionLost){
            // 啟動 跳轉連結錯誤
            Log.d("ed43", "Receiver Connection Lost");
            activity.connection_lost();
        }

        // 接收訊息 : 設備已就緒
        boolean isReadyForScale = intent.getBooleanExtra("isReadyForScale", false);
        if(isReadyForScale) {
            // 啟動 對話框提示操作
            activity.alertMessage(0);
        }

        // 接收訊息 : 數據更新
        boolean updateData = intent.getBooleanExtra("updateData", false);
        if(updateData) {
            // 啟動 更新顯示數據
            activity.setResult(intent.getDoubleArrayExtra("data"));
        }

    }
}
