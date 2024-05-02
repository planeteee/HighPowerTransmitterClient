package com.xing.hptc;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xing.hptc.view.ElectrDashboardView;
import com.xing.hptc.view.WifiActivity;
import com.xing.net.HptcProtocol;
import com.xing.net.SocketClientHptc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private  static final String TAG="HPTC MainActivity";
    private static final String DEVICE_NAME_CHARS = "FSG_";
    private String ip;
    private int port;
    private Socket socket;

    //wifi
    WifiManager wifiManager;

    private SocketClientHptc socketClientHptc;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private ElectrDashboardView voltage_dashboard;

    private Timer timer;

    /*----------------- UI控件 ----------------*/
    private Button btn_transmit;
    private Button btn_start;
    private Button btn_test;
    private CheckBox cb_connect;
    private LinearLayout ll_wifi;
    private TextView tv_device_name;
    private Button btn_getDeviceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* 隐藏标题栏 */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐离导航栏(可选)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        voltage_dashboard=findViewById(R.id.voltage_dashboard);
        voltage_dashboard.setNum(-0);

        btn_test=findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_test=findViewById(R.id.et_test);
                String testValueStr=et_test.getText().toString();
                int testValue=Integer.valueOf(testValueStr);
                //voltage_dashboard.setNum(testValue);
                float testValueF=Float.valueOf(testValue);
                voltage_dashboard.setNumAnimator(testValueF);
                //voltage_dashboard.setCompleteDegree(testValueF);
            }
        });


        //连接复选框
        cb_connect=findViewById(R.id.cb_connect);
        cb_connect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    startCommunicate();
                }else {
                    if(socketClientHptc!=null){
                        socketClientHptc.close();
                        socketClientHptc=null;
                    }
                }
            }
        });
        cb_connect.setVisibility(View.GONE);
        ll_wifi=findViewById(R.id.ll_wifi);
        ll_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startWifiAct();
                openWifiSettings();
            }
        });
        tv_device_name=findViewById(R.id.tv_device_name);

        //wifi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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
                        String ssid=wifiInfo.getSSID();
                        ssid=ssid.replaceAll("^\"|\"$", "");//去掉引号
                        Log.d(TAG, "onReceive ssid: "+ssid);
                        if(!ssid.contains(DEVICE_NAME_CHARS)){
                            setDeviceName(ssid);
                            cb_connect.setVisibility(View.VISIBLE);
                            //setDeviceName("未连接，点击此处设置。");
                        }else {
                            setDeviceName(ssid);
                        }
                    } else {
                        // Wi-Fi 未连接
                        // 这里可以添加处理断开连接后的逻辑
                    }
                }
            }
        }, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

        //通信测试
        btn_start=findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCommunicate();
            }
        });
        //开始发射
        btn_transmit=findViewById(R.id.btn_transmit);
        btn_transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String txt= btn_transmit.getText().toString();
                        if(txt.startsWith("开始")){
                            if(socketClientHptc!=null){
                                socketClientHptc.requestStartMeasure();

                            }
                        }else if(txt.startsWith("停止")){
                            if(socketClientHptc!=null){
                                socketClientHptc.requestStopMeasure();
                            }
                        }

                    }
                });
            }
        });
        //获取参数
        btn_getDeviceStatus=findViewById(R.id.btn_getDeviceStatus);
        btn_getDeviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(socketClientHptc!=null){
                            socketClientHptc.requestGetDeviceStatus();
                        }
                    }
                });
            }
        });
        // 创建一个定时器
        timer = new Timer();
        // 设置定时任务
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // 在定时器线程中执行的任务
                // 这里可以执行你需要定时执行的操作

                // 为了在定时器线程中更新UI，你需要使用Handler
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 在主线程中更新UI
                        //Toast.makeText(getApplicationContext(),"定时执行。",Toast.LENGTH_SHORT).show();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if(socketClientHptc!=null){
                                    if(socketClientHptc.isConnected())
                                        socketClientHptc.requestGetDeviceStatus();
                                }
                            }
                        });
                    }
                });
            }
        };

        // 启动定时器，延迟0毫秒后开始，每隔1000毫秒执行一次
        timer.schedule(timerTask, 0, 1000*3);

    }



    private void startCommunicate(){
        if(socketClientHptc==null){
            socketClientHptc=new SocketClientHptc("192.168.3.121",3000,new Handler(){
                String msgObjStr="";

                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    //msg.what
                    Log.d(TAG, "handleMessage: "+msg);
                    if(msg.obj!=null)
                        msgObjStr=msg.obj.toString();
                    //发生错误
                    if(msg.what== HptcProtocol.MessageType.ERROR.ordinal()){
                        //断开连接了
                        if(msgObjStr.equals(HptcProtocol.DeviceAction.DISCONNECTED.toString())){
                            Toast.makeText(getApplicationContext(),"连接已断开。",Toast.LENGTH_SHORT).show();
                            if(socketClientHptc!=null){
                                socketClientHptc.close();
                                socketClientHptc=null;
                            }
                        }else if(msgObjStr.equals(HptcProtocol.DeviceAction.CONNECT_FAIL.toString())){
                            Toast.makeText(getApplicationContext(),"连接失败，请检查设备。",Toast.LENGTH_SHORT).show();
                            if(socketClientHptc!=null){
                                socketClientHptc.close();
                                socketClientHptc=null;
                            }
                        }
                    }
                    //命令执行成功
                    else if(msg.what== HptcProtocol.MessageType.ACTION_OK.ordinal()){
                        if(msgObjStr.equals(HptcProtocol.DeviceAction.SHAKE_HANDS.toString())){
                            Toast.makeText(getApplicationContext(),"连接成功。",Toast.LENGTH_SHORT).show();
                        } else if (msgObjStr.equals(HptcProtocol.DeviceAction.MEASURE_START)) {
                            runOnUiThread(()->{
                                btn_transmit.setText("停止测量");
                            });
                            Toast.makeText(getApplicationContext(),"设备已开始测量",Toast.LENGTH_SHORT).show();

                        } else if (msgObjStr.equals(HptcProtocol.DeviceAction.MEASURE_STOP)) {
                            runOnUiThread(()->{
                                btn_transmit.setText("开始测量");
                            });
                            Toast.makeText(getApplicationContext(),"设备已停止测量",Toast.LENGTH_SHORT).show();

                        } else if(msgObjStr.contains(HptcProtocol.DeviceAction.REQUEST_STATUS.toString())){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String txt=msgObjStr.split("==")[1];
                                    String[] valuse=txt.split(";");
                                    String deviceId=valuse[0];
                                    String voltage=valuse[1];
                                    String current=valuse[2];
                                    String temp=valuse[3];
                                    String battery=valuse[4];
                                    String power=valuse[5];
                                    String res=valuse[6];

                                    TextView tv_deviceId=findViewById(R.id.tv_deviceId);
                                    tv_deviceId.setText(deviceId);
                                    TextView tv_voltage=findViewById(R.id.tv_voltage);
                                    tv_voltage.setText(voltage);
                                    TextView tv_current=findViewById(R.id.tv_current);
                                    tv_current.setText(current);
                                    TextView tv_power=findViewById(R.id.tv_power);
                                    tv_power.setText(power);
                                    TextView tv_temp=findViewById(R.id.tv_temp);
                                    tv_temp.setText(temp);
                                    TextView tv_battery=findViewById(R.id.tv_battery);
                                    tv_battery.setText(battery);
                                    TextView tv_res=findViewById(R.id.tv_res);
                                    tv_res.setText(res);
                                    ElectrDashboardView voltage_dashboard= findViewById(R.id.voltage_dashboard);
                                    voltage_dashboard.setNumAnimator(Integer.parseInt(voltage));
                                    Toast.makeText(getApplicationContext(),msgObjStr,Toast.LENGTH_SHORT).show();
                                }
                            });
                            //Toast.makeText(getApplicationContext(),msgObjStr,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            socketClientHptc.execute();
        }
    }

    private void startWifiAct(){
        Intent intent = new Intent(this, WifiActivity.class);
        intent.putExtra(EXTRA_MESSAGE,"CODE");
        startActivity(intent);
    }
    private void socketCommunicate() {
        try {
            socket = new Socket(ip, port);
            // 获取输出流，用于向服务器发送数据
            OutputStream outputStream = socket.getOutputStream();

            // 获取输入流，用于接收服务器发送的数据
            InputStream inputStream = socket.getInputStream();

            // 发送数据到服务器
            String dataToSend = "Hello, Server!";
            outputStream.write(dataToSend.getBytes());

            // 从服务器接收数据
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String receivedData = new String(buffer, 0, bytesRead);


        } catch (IOException e) {
            Log.e(TAG, "socketCommunicate: IOException", e);
        } finally {
            if(socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }


    //设置设备名称
    private void setDeviceName(String info){
        tv_device_name.setText(info);
    }

    private void openWifiSettings() {
        Intent intent = new Intent();

        // For Android 9 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            intent.setAction(Settings.ACTION_WIFI_SETTINGS);
        } else {
            // For Android 8 and below
            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        }

        // Start the activity
        startActivity(intent);
    }

}