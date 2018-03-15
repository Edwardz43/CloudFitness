package tw.brad.apps.cloudfitness.java_class;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import java.util.UUID;

import tw.brad.apps.cloudfitness.ErrorActivity;

import static com.inuker.bluetooth.library.Constants.REQUEST_FAILED;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * Created by EdLo on 2018/3/15.
 */

public class BLEService extends Service{
    static boolean isBluetoothOpen;
    static boolean isDeviceConnect;
    static boolean isDeviceSearched;
    private String deviceMAC;
    private BluetoothClient mClient;
    private static BleConnectStatusListener mBleConnectStatusListener;
    private static BluetoothStateListener mBluetoothStateListener;

    // GATT Service UUID
    private final UUID serviceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    // character UUID 通知/寫入
    private final UUID notifyUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private final UUID writeUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    // 回應體重數據的回傳
    private final byte[] weightResponse = new byte[]{90,-46,0,0,0,0,0,0,0,0,0,0,-120,-86};
    //private final byte[] profileResponse = new byte[]{90,-43,0,0,0,0,0,0,0,0,0,0,-113,-86};

    // 使用者基本資料 由ResultActivity傳過來
    private byte[] userProfile;

    // 記錄體脂計回傳數據
    private double[] data;
    private double weight = 0;

    @Override
    public void onCreate() {
        Log.d("ed43", "service onCreate");
        super.onCreate();
        init();
    }

    // 服務初始化
    private void init() {
        //Log.d("ed43", "service init()");
        // 藍芽開啟 : 否
        isBluetoothOpen = false;

        // 設備是否搜索到 : 否
        isDeviceSearched = false;

        // 設備連結 : 否
        isDeviceConnect = false;

        // 記錄體脂計回傳值 包括 : 體重 體脂 水分 肌肉質量 骨質輛 內臟脂肪 BMI
        data = new double[7];

        mClient = new BluetoothClient(this);

        //開啟藍芽
        mClient.openBluetooth();

        // 偵測 BLE 狀態Listener
        mBluetoothStateListener = new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                if(openOrClosed){
                    //偵測到藍芽開啟 發放廣播
                    isBluetoothOpen = true;
                    Log.d("ed43", "BLE:openOrClosed: " + openOrClosed);
                    Intent localIntent = new Intent("BleService");
                    localIntent.putExtra("isBluetoothOpen", isBluetoothOpen);
                    sendBroadcast(localIntent);
                }
            }
        };
        // 註冊 BLE 狀態Listener
        mClient.registerBluetoothStateListener(mBluetoothStateListener);
    }

    // 服務接收指令
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isBluetoothOpen){
            init();
        }

        if (intent != null) {
            int cmd = intent.getIntExtra("cmd", -1);
            String id = intent.getStringExtra("id");
            switch (cmd){
                // 搜索
                case 0:
                    Log.d("ed43", "service cmd : search");
                    search();
                    break;
                // 連接設備
                case 1:
                    //Log.d("ed43", "service cmd : connect");
                    userProfile = intent.getByteArrayExtra("profile");
                    connect(id);
                    break;
                // 斷線
                case 2:
                    disconnect();
                    break;
            }
        }
        // START_NOT_STICKY : 使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
        return START_NOT_STICKY;
    }

    // 搜索
    private void search() {
        isDeviceSearched = false;
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 3)
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                // 開始搜索
                //Log.d("ed43", "onSearchStarted");
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                // 搜索到藍芽設備
                //Log.d("ed43", "Device Founded");

                // 如果之前尚未搜索到 且 搜到的設備符合條件
                if(!isDeviceSearched && device.getName().substring(0, 3).equals("MCF")){
                    // 發送廣播並將設備的MAC回傳到Activity
                    isDeviceSearched = true;
                    Intent it = new Intent("BleService");
                    it.putExtra("isDeviceSearched", isDeviceSearched);
                    it.putExtra("device", device);
                    sendBroadcast(it);
                }
            }

            @Override
            public void onSearchStopped() {
                // 停止搜索
                //Log.d("ed43", "Search Stopped");
                if(!isDeviceSearched){
                    Intent it = new Intent("BleService");
                    it.putExtra("deviceNotFound", true);
                    sendBroadcast(it);
                }
            }

            @Override
            public void onSearchCanceled() {
                //Log.d("ed43", "Search Canceled");
            }
        });
    }

    // 連接設備
    private void connect(String deviceMAC) {
        connectDevice(deviceMAC);
        this.deviceMAC = deviceMAC;

        // 偵測 Connect 狀態Listener
        mBleConnectStatusListener = new BleConnectStatusListener() {
            @Override
            public void onConnectStatusChanged(String mac, int status) {
                if (status == STATUS_CONNECTED) {
                    //Log.d("ed43", "Connect Status Changed : CONNECTED");
                } else if (status == STATUS_DISCONNECTED) {
                    //Log.d("ed43", "Connect Status Changed : DISCONNECTED");
                    //斷線
                    Intent it = new Intent("BleService");
                    it.putExtra("connectError", true);
                    sendBroadcast(it);
                }
            }
        };
        // 註冊 Connect 狀態 Listener
        mClient.registerConnectStatusListener(deviceMAC, mBleConnectStatusListener);

    }

    // 與設備中斷連結
    private void disconnect() {
        // 停止搜索
        mClient.stopSearch();
        if(isBluetoothOpen){
            if (this.deviceMAC != null) {
                // 中斷連結
                mClient.disconnect(deviceMAC);
            }
            // 關閉藍芽
            mClient.closeBluetooth();
        }
        // 取消註冊監聽器
        mClient.unregisterConnectStatusListener(deviceMAC, mBleConnectStatusListener);
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
    }

    // 開啟通知
    private void mNotify() {
        //Log.d("ed43", "mNotify()");
        mClient.notify(deviceMAC, serviceUUID, notifyUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                // 先將設備傳過來的byte[]轉成16進制
                String mData = toHexString(value);

                // 通知狀態 : 更新鎖定體重及阻抗
                if(mData.substring(2,4).equals("D2")){
                    weight = getWeight(mData);
                    // 回傳MCU 表示收到穩定體重
                    write(weightResponse);
                } else if(mData.substring(2,4).equals("D3")){
                    // 通知狀態 : 上傳體脂測試結果
                    getData(mData);
                    Intent it = new Intent("BleService");
                    // 發送廣播 : 資料已更新
                    it.putExtra("updateData", true);
                    it.putExtra("data", data);
                    sendBroadcast(it);
                }else if(mData.substring(2,4).equals("D5")){
                    // 通知狀態 : MCU->APP應答用戶數據 表示寫入資料成功
                    Intent it = new Intent("BleService");
                    // 發送廣播 : 設備就緒
                    it.putExtra("isReadyForScale", true);
                    sendBroadcast(it);
                }
            }
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    //Log.d("ed43", "Notify : REQUEST_SUCCESS");
                }else if(code == REQUEST_FAILED){
                    //Log.d("ed43", "Notify : REQUEST_FAILED");
                }
            }
        });
    }

    // 寫入 character
    private void write(byte[] bytes) {
        mClient.write(deviceMAC, serviceUUID, writeUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                //Log.d("ed43", "write response code : " + code);
                if (code == REQUEST_SUCCESS) {
                    //Log.d("ed43", "OK");
                }else if(code == REQUEST_FAILED){
                    //Log.d("ed43", "FAILED");
                }
            }
        });
    }

    // 連結設備 : activity回傳的MAC連結
    private boolean connectDevice(String deviceMAC){
        if (deviceMAC == null) {
            return false;
        }
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3).setConnectTimeout(30000)
                .setServiceDiscoverRetry(3).setServiceDiscoverTimeout(20000)
                .build();
        mClient.connect(deviceMAC, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                //Log.d("ed43","connectDevice Response");
                if(code == REQUEST_SUCCESS){
                    // 連結成功 開啟通知
                    mNotify();
                    // 寫入使用者資料
                    write(userProfile);
                }else if (code == REQUEST_FAILED){
                    //Log.d("ed43","Connect : REQUEST_FAILED");
                    // 連結失敗
                    Intent it = new Intent("BleService");
                    it.putExtra("connectError", true);
                    sendBroadcast(it);
                }
            }
        });
        return true;
    }

    // 將byte[]轉為16進制string
    public String toHexString (byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // 將16進制的string轉為10進制 取得體重數據
    public double getWeight(String s) {
        int highDigit = Integer.parseInt(s.substring(5, 6), 16);
        int midDigit = Integer.parseInt(s.substring(6, 7), 16);
        int lowDigit = Integer.parseInt(s.substring(7, 8), 16);
        return (highDigit * 16 * 16 + midDigit * 16 + lowDigit)/10.0;
    }

    // 將16進制的string轉為10進制 取得體脂測試結果
    public void getData(String s) {
        data[0] = weight;

        int highFat = Integer.parseInt(s.substring(5, 6), 16);
        int midFat = Integer.parseInt(s.substring(6, 7), 16);
        int lowFat = Integer.parseInt(s.substring(7, 8), 16);
        double digitFat = (highFat * 256 + midFat * 16 +lowFat)/10.0;
        data[1] = digitFat;

        int highWater = Integer.parseInt(s.substring(9, 10), 16);
        int midWater = Integer.parseInt(s.substring(10, 11), 16);
        int lowWater = Integer.parseInt(s.substring(11, 12), 16);
        double digitWater = (highWater * 256 + midWater * 16 +lowWater)/10.0;
        data[2] = digitWater;

        int highMuscle = Integer.parseInt(s.substring(13, 14), 16);
        int midMuscle = Integer.parseInt(s.substring(14, 15), 16);
        int lowMuscle = Integer.parseInt(s.substring(15, 16), 16);
        double digitMuscle = (highMuscle * 256 + midMuscle * 16 +lowMuscle)/10.0;
        data[3] = digitMuscle;

        int highBone = Integer.parseInt(s.substring(16, 17), 16);
        int lowBone = Integer.parseInt(s.substring(17, 18), 16);
        double digitBone = (highBone * 16 +lowBone)/10.0;
        data[4] = digitBone;

        int highVfat = Integer.parseInt(s.substring(18, 19), 16);
        int lowVfat = Integer.parseInt(s.substring(19, 20), 16);
        double digitVfat = highVfat * 16 +lowVfat;
        data[5] = digitVfat;

        int highBMI = Integer.parseInt(s.substring(21, 22), 16);
        int midBMI = Integer.parseInt(s.substring(22, 23), 16);
        int lowBMI = Integer.parseInt(s.substring(23, 24), 16);
        double digitBMI = (highBMI * 256 + midBMI * 16 +lowBMI)/10.0;
        data[6] = digitBMI;
    }

    // 停止服務
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("ed43", "service onDestroy");
        disconnect();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
