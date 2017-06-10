package gjcm.kxf.kxfservice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kxf on 2016/12/17.
 * 蓝牙service
 */
public class BluetoothService extends Service {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

    private boolean scanning;

    private Map<String, BluetoothGatt> bluetoothGattMap;

    private Timer mRssiTimer;

    private ExecutorService readWriteWorker = Executors.newSingleThreadExecutor();
    private volatile BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private volatile BluetoothGatt bluetoothGatt;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        bluetoothManager=(BluetoothManager)getSystemService(Service.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
        // 若蓝牙为打开，就会提示用户启动蓝牙服务
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
            return;
        }
        bluetoothGattMap=new HashMap<String, BluetoothGatt>();
        handler=new Handler();
        super.onCreate();
    }
    private void startFindDevice() {
        if (!bluetoothAdapter.isDiscovering()) {
            scanLeDevice(true);
        }
    }
    public void stopFindDevice() {
        scanLeDevice(false);
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, 10000);
            scanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback=new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] arg2) {
            // TODO Auto-generated method stub
            //			device  搜索到的设备
            //			rssi 设备的rssi 也就是当前的设备信号的强弱
        }
    };

    private boolean connect(String address) {
        stopFindDevice();
        if ((bluetoothAdapter == null) || (address == null)) {
            return false;
        }
        BluetoothGatt gatt = (BluetoothGatt) bluetoothGattMap.get(address);
        if (gatt != null) {
            gatt.close();
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        gatt = device.connectGatt(this, false, bluetoothGattCallback);
        bluetoothGattMap.put(address, gatt);
        return true;
    }
    public boolean disconnect(String address) {
        BluetoothGatt gatt = (BluetoothGatt) bluetoothGattMap.get(address);
        if (gatt != null) {
            gatt.close();
        }
        bluetoothGattMap.remove(address);
        return true;
    }

    BluetoothGattCallback bluetoothGattCallback=new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt,
                                            int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//				连接成功时获取特征值
                gatt.discoverServices();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
//						我不停的读取，不停的读取，时间频率自己控制就行
                        gatt.readRemoteRssi();
                    }
                };
                mRssiTimer = new Timer();
                mRssiTimer.schedule(task, 1000, 1000);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mRssiTimer != null) {
                    mRssiTimer.cancel();
                }
            }
        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // 动态获取 rssi 值
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt=gatt;
                showService(gatt.getServices());
            } else {
            }
        }


    };
    private void showService(List<BluetoothGattService> list){
        if (list == null) return;
        for (BluetoothGattService gattService : list) {
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for ( final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
                if(gattCharacteristic.getUuid().toString().equalsIgnoreCase("0000fff1-0000-1000-8000-00805f9b34fb")){
//					  这是我的特征值
                }
            }

        }
    }

    private void writeData(){
        byte[] value = new byte[]{(byte) 0x01,(byte) 0x01,(byte) 0x01,(byte) 0x01};
        bluetoothGattCharacteristic.setValue(value);
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

}