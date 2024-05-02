package com.xing.hptc.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xing.common.PrivateSaveUtil;
import com.xing.hptc.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WifiActivity extends AppCompatActivity {

    private static final String TAG = "HPTC WifiActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    //设备名字的特征，用于判断是否为需要的设备
    private static final String DEVICE_NAME_CHARS = "FSG_";
    private static final String KEY_WIFI_PASSWORD = "wifi_password";
    private static final String KEY_WIFI_SSID = "wifi_ssid";

    private WifiManager wifiManager;
    private List<ScanResult> scanResults;

    private Timer timer;
    private Spinner spinner_wifi;
    private ArrayAdapter adapter_wifi;
    private Button btn_wifi_connect,btn_wifi_refresh;
    private TextView tv_wifi_status;
    private EditText et_wifi_password;

    private String current_ssid ="";
    private String wifi_password="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* 隐藏标题栏 */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐离导航栏(可选)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //读取存储的值
        PrivateSaveUtil.init(WifiActivity.this);

        //返回图标
        ImageView iv_wifi_back=findViewById(R.id.iv_wifi_back);
        iv_wifi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiActivity.this.finish();
            }
        });
        //下拉列表
        spinner_wifi=findViewById(R.id.spinner_wifi);
        adapter_wifi=new ArrayAdapter(this,android.R.layout.simple_spinner_item);
        adapter_wifi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_wifi.setAdapter(adapter_wifi);
        // 设置下拉列表选择事件
        spinner_wifi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 在此处处理选择 Wi-Fi 网络的逻辑
                try {
                    TextView item= (TextView) selectedItemView.findViewById(android.R.id.text1);
                    current_ssid =item.getText().toString();
                }catch (Exception e){
                    current_ssid ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 在此处处理没有选择任何项的逻辑
            }
        });

        //连接wifi按钮
        btn_wifi_connect=findViewById(R.id.btn_wifi_connect);
        btn_wifi_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=btn_wifi_connect.getText().toString();
                if(text.equals("连接")){
                    if(current_ssid ==""){
                        runOnUiThread(() -> Toast.makeText(WifiActivity.this, "未选择wifi", Toast.LENGTH_SHORT).show());
                    }else {
                        wifi_password=et_wifi_password.getText().toString();
                        if(wifi_password==""){
                            runOnUiThread(() -> Toast.makeText(WifiActivity.this, "未设置密码", Toast.LENGTH_SHORT).show());
                        }else {
                            //connectToWifi(current_ssid,"q0q19556.");
                            connectToWifi(current_ssid,wifi_password);
                        }
                    }
                }else  if(text.equals("断开")){
                    //disconnectFromWifi();
                }

            }
        });
        //刷新wifi
        btn_wifi_refresh=findViewById(R.id.btn_wifi_refresh);
        btn_wifi_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               scanWifiNetworks();
            }
        });

        //wifi状态提示信息
        tv_wifi_status=findViewById(R.id.tv_wifi_status);

        //wifi密码
        et_wifi_password=findViewById(R.id.et_wifi_password);
        wifi_password=PrivateSaveUtil.getString(KEY_WIFI_PASSWORD);
        et_wifi_password.setText(wifi_password);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // 注册 Wi-Fi 扫描结果的广播接收器
        registerReceiver(new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    scanResults = wifiManager.getScanResults();

                }else {
                    scanResults = wifiManager.getScanResults();
                }
                updateWifiList();
                /*
                if(!current_ssid.contains(DEVICE_NAME_CHARS)){
                    updateWifiList();
                }else {
                    boolean notToUpdate=false;
                    for (ScanResult scanResult : scanResults) {
                        if(scanResult.SSID.contains(current_ssid)){
                            notToUpdate=true;
                            break;
                        }
                    }
                    if(!notToUpdate){
                        updateWifiList();
                    }
                }

                 */
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // 注册 Wi-Fi 连接状态的广播接收器
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    // Wi-Fi 网络状态发生变化
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Wi-Fi 已连接
                        // 这里可以添加处理连接后的逻辑
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        current_ssid=wifiInfo.getSSID();
                        current_ssid=current_ssid.replaceAll("^\"|\"$", "");//去掉引号
                        if(!current_ssid.contains(DEVICE_NAME_CHARS)){
                            wifiManager.disconnect();
                            runOnUiThread(()->setWifiStatusInfo("未连接。"));
                        }else {
                            runOnUiThread(()->setWifiStatusInfo(current_ssid+" 已连接。"));
                            //runOnUiThread(()->{btn_wifi_connect.setText("断开");});
                            //连接成功，保存参数
                            PrivateSaveUtil.saveString(KEY_WIFI_PASSWORD,wifi_password);
                            PrivateSaveUtil.saveString(KEY_WIFI_SSID,current_ssid);
                        }
                    } else {
                        current_ssid="";
                        // Wi-Fi 未连接
                        // 这里可以添加处理断开连接后的逻辑
                        runOnUiThread(()->setWifiStatusInfo("未连接。"));
                    }
                }
            }
        }, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));


        Button btn_test = findViewById(R.id.btn_wifi_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //scanWifiNetworks();
            }
        });

        // 检查并请求 Wi-Fi 权限（在 AndroidManifest.xml 中也需要声明）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Request permissions
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},PERMISSION_REQUEST_CODE);
            }
        }

        scanWifiNetworks();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scanWifiNetworks() {
        wifiManager.setWifiEnabled(false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        runOnUiThread(()->setWifiStatusInfo("刷新wifi..."));
        wifiManager.startScan();
    }


    //更新下拉列表
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateWifiList() {
        adapter_wifi.clear();
        //移除不相干的设备
        scanResults.removeIf(i->!i.SSID.contains(DEVICE_NAME_CHARS));
        if(scanResults.size()==0){
            runOnUiThread(()->setWifiStatusInfo("未扫描到设备，请关闭手机wifi重新打开。"));
        }else {
            boolean needToSelect=true;  //要不要提醒选择设备
            for (ScanResult scanResult : scanResults) {
                adapter_wifi.add(scanResult.SSID);
                if(scanResult.SSID.contains(current_ssid)){
                    needToSelect=false;
                }
            }
            if(needToSelect){
                runOnUiThread(()->setWifiStatusInfo("请选择设备连接。"));
            }else {
                runOnUiThread(()->setWifiStatusInfo(current_ssid+" 已连接。"));
            }
        }
    }

    private void connectToWifi(String ssid, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectToWifiApi29AndAbove(ssid, password);
        } else {
            connectToWifiApi28AndBelow(ssid, password);
        }
    }

    //连接到安卓10及以上
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectToWifiApi29AndAbove(String ssid, String password) {
        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build();

        NetworkRequest.Builder requestBuilder = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            connectivityManager.requestNetwork(requestBuilder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    // Wi-Fi network is available
                    // Optional: Trigger a reconnection to the Wi-Fi network
                    //runOnUiThread(()->setWifiStatusInfo("正在连接..."));
                    wifiManager.reconnect();
                    wifiManager.setWifiEnabled(false);
                    // Notify the user
                    //runOnUiThread(() -> Toast.makeText(WifiActivity.this, "Connecting to Wi-Fi...", Toast.LENGTH_SHORT).show());
                    //
                }

                @Override
                public void onLost(Network network) {
                    // Wi-Fi network is lost
                    //Toast.makeText(WifiActivity.this, "Wi-Fi network is lost.", Toast.LENGTH_SHORT).show();
                    runOnUiThread(()->setWifiStatusInfo("已断开连接。"));

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "connectToWifiApi29AndAbove exception: "+e.getMessage());
        }
    }

    //连接到安卓9及以下
    private void connectToWifiApi28AndBelow(String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\""; // For WPA-PSK security

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        // Notify the user
        Toast.makeText(this, "Connecting to Wi-Fi...", Toast.LENGTH_SHORT).show();
    }

    private  void disconnectWifi(){
        wifiManager.disconnect();
    }

    //设置wifi状态信息
    private void setWifiStatusInfo(String status){
        tv_wifi_status.setText(status);
    }
}