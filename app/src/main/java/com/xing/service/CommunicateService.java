package com.xing.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.xing.net.SocketClientHptc;


public class CommunicateService extends Service {
    private SocketClientHptc mSocketClientHptc;

    public CommunicateService() {
    }
    private final IBinder binder = new LocalBinder();

    // 绑定服务时返回的 Binder
    public class LocalBinder extends Binder {
        public CommunicateService getService() {
            return CommunicateService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在服务启动时创建Socket
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        // 如果服务因内存不足而被杀死，则在资源可用时重启服务
        //return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
}