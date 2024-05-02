package com.xing.net;




import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xing.common.LogToFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SocketClientHptc extends AsyncTask {


    //当前发送的数据
    private byte[] currentSendData;
    /**
     * 当前发送的包的类型
     */
    private byte[] currentSendDataType;

    private static final String TAG = "HPTC SocketClientHptc";
    private Socket mSocket = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    private Boolean isConnected = false;
    private String mIP;
    private int mPort = 12345;
    private Handler mHandler = null;

    //
    private HptcProtocol hptcProtocol;

    private ArrayList<HptcProtocol.ReceivedData> listExportData;

    public SocketClientHptc(String ip, int port, Handler handler) {
        mIP = ip;
        mPort = port;
        mHandler = handler;
        hptcProtocol=new HptcProtocol();
        listExportData=new ArrayList<HptcProtocol.ReceivedData>();
    }

    public void run(){
        System.out.println("SocketClientHptc: " + Thread.currentThread().getName());
        Log.d(TAG, "run ");

        try {
            //指定ip地址和端口号
            mSocket = new Socket(mIP, mPort);

            //获取输出流、输入流
            mOutStream = mSocket.getOutputStream();
            mInStream = mSocket.getInputStream();

            isConnected = true;
            //writeMsgInternal("### A message from MyClientSocket.");
            requestShakeHands();

            readMsgInternal();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "run Exception: "+e.getMessage());
            Log.d(TAG, "run Exception: "+e.getStackTrace());
            //mHandler.sendEmptyMessage(EnumSockteResult.MSG_SOCKET_CONNECTFAIL.ordinal());
            sendMessage(HptcProtocol.MessageType.ERROR.ordinal(), HptcProtocol.DeviceAction.CONNECT_FAIL.toString());

            isConnected = false;
            return;
        }

        mHandler.sendEmptyMessage(EnumSockteResult.MSG_SOCKET_CONNECTOK.ordinal());
    }



    private void readMsgInternal() {

        while (isConnected) {
            byte[] buffer = new byte[1024];
            //循环执行read，用来接收数据。
            //数据存在buffer中，count为读取到的数据长度。
            try {
                int count = mInStream.read(buffer);

                byte[] dataArr= Arrays.copyOfRange(buffer,0,count);
                LogToFile.d(TAG, "receivedData: :"+byteArrayToHexString(dataArr));
                //握手成功
                if(isSameArray(currentSendData,HptcProtocol.COM_CLIENT_SHAKE_HANDS)){
                    Log.d(TAG, "readMsgInternal: 握手成功1");
                    if(isSameArray(dataArr, HptcProtocol.COM_SERVER_SHAKE_HANDS_SUCCESS)){
                        sendMessage(HptcProtocol.MessageType.ACTION_OK.ordinal(), HptcProtocol.DeviceAction.SHAKE_HANDS.toString());
                    }
                }
                else {
                    //解包
                    HptcProtocol.ReceivedData rd= hptcProtocol.unpack(dataArr);
                    if(rd!=null){
                        //开始测量
                        if(isSameArray(currentSendDataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE)&&
                        isSameArray(currentSendData,HptcProtocol.COM_CLIENT_MEASURE_START_DATA)){
                            //sendMessage(HptcProtocol.DeviceAction.MEASURE_START.ordinal(),"请求开始测量成功");
                            sendMessage(HptcProtocol.MessageType.ACTION_OK.ordinal(), HptcProtocol.DeviceAction.MEASURE_START.toString());
                        }
                        //停止测量
                        else if(isSameArray(currentSendDataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE)&&
                                isSameArray(currentSendData,HptcProtocol.COM_CLIENT_MEASURE_STOP_DATA)){
                            //sendMessage(HptcProtocol.DeviceAction.MEASURE_STOP.ordinal(),"请求停止测量成功");
                            sendMessage(HptcProtocol.MessageType.ACTION_OK.ordinal(), HptcProtocol.DeviceAction.MEASURE_STOP.toString());
                        }
                        //设备状态
                        else if(isSameArray(currentSendDataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_DEVICE_STATUS)){
                            String str_deviceId="str_deviceId";
                            if(rd.status== HptcProtocol.CommunicateStatus.OK_UNPACK){
                                if(rd.data==null){
                                    continue;
                                }
                                String  deviceId=getDeviceId(new byte[]{rd.data[3],rd.data[2],rd.data[1],rd.data[0]});
                                long voltage=getVoltage(new byte[]{rd.data[42],rd.data[41],rd.data[40],rd.data[39]});
                                long current=getVoltage(new byte[]{rd.data[29],rd.data[28],rd.data[27],rd.data[26]});
                                int temp=rd.data[25];
                                int baterry=rd.data[23];
                                long power=voltage*(current/1000);
                                String str_res="-";
                                if(current!=0){
                                    long res=voltage/(current/1000);
                                    str_res=String.valueOf(res);
                                }
                                String txt=deviceId+";"+String.valueOf(voltage)+";"+String.valueOf(current)+";"+String.valueOf(temp)+";"+String.valueOf(baterry)+";"+String.valueOf(power)+";"+String.valueOf(str_res);
                                //str_deviceId=String.valueOf(deviceId);
                                sendMessage(HptcProtocol.MessageType.ACTION_OK.ordinal(), HptcProtocol.DeviceAction.REQUEST_STATUS.toString()+"=="+txt);

                            }
                            //sendMessage(HptcProtocol.MessageType.ACTION_OK.ordinal(), HptcProtocol.DeviceAction.REQUEST_STATUS.toString()+"++++"+str_deviceId);

                        }
                        //导出数据
                        else if (isSameArray(currentSendDataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_EXPORT_DATA)) {
                            if(rd.status==HptcProtocol.CommunicateStatus.OK_UNPACK){
                                if(rd.data==null){
                                    continue;
                                }
                                int dataNumber=rd.data.length/8;

                            }
                        }

                    }else {
                        //sendMessage(HptcProtocol.DeviceAction.UNKNOWN_ACTION.ordinal(),"未知动作");
                        sendMessage(HptcProtocol.MessageType.ERROR.ordinal(), HptcProtocol.DeviceAction.UNKNOWN_ACTION.toString());
                    }


                    /*
                    if(isSameArray(currentSendDataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE)){

                    }

                    //请求处理成功
                    if(rd.status.equals(HptcProtocol.CommunicateStatus.OK_UNPACK)){
                        //开始测量的响应
                        if(isSameArray(rd.dataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE)){
                            sendMessage(HptcProtocol.MessageType.ACTION_OK.ordinal(),"");
                        }
                    }
                    //请求处理异常
                    else {
                        //sendMessage(HptcProtocol.CommunicateStatus.);
                    }

                     */
                }
                /*
                else if(isSameArray(currentSendDataType,HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE)){
                    HptcProtocol.ReceivedData rd= hptcProtocol.unpack(dataArr);
                    if(rd.status.equals(HptcProtocol.UnpackStatus.OK)){
                        if(isSameArray(rd.dataType,))
                    }
                }

                 */

                //String str = new String(buffer, "UTF-8");
                //Log.d(TAG, "readMsgInternal buffer:"+byteArrayToHexString(dataArr));
                //Message msg = new Message();

            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                sendMessage(HptcProtocol.MessageType.ERROR.ordinal(), HptcProtocol.DeviceAction.DISCONNECTED.toString());
            }
        }
    }




    private void sendBytes(byte[] data){
        if(mOutStream==null){
            return;
        }
        try{
            Log.d(TAG, "sendBytes data:"+byteArrayToHexString(data));
            mOutStream.write(data);
            mOutStream.flush();
            currentSendData=data;
        }catch (Exception e){
            e.printStackTrace();
            isConnected = false;
            Log.e(TAG, "sendBytes exception: "+e.getStackTrace() );
            Log.e(TAG, "sendBytes exception: "+e.getMessage() );
        }
    }

    /**
     * 发送消息
     * @param msg
     */
    private void sendMessage(int what,String msg){
        Log.d(TAG, msg);
        Message message = new Message();
        message.what =what;// EnumSockteResult.MSG_SOCKET_READ.ordinal();
        message.obj = msg;
        if(mHandler != null) mHandler.sendMessage ( message );
    }
    private void writeMsgInternal(String msg){
        if(msg.length() == 0 || mOutStream == null)
            return;
        try {   //发送
            Log.d(TAG, "writeMsgInternal msg:"+msg);
            mOutStream.write(msg.getBytes());
            mOutStream.flush();
        }catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        }
    }


    public void writeMsg(String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeMsgInternal(str);
            }
        }).start();
    }

    public void close(){
        isConnected = false;

        try {
            if (mOutStream != null) {
                mOutStream.close();
                mOutStream = null;
            }
            if (mInStream != null) {
                mInStream.close();
                mInStream = null;
            }
            if(mSocket != null){
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isConnected(){
        return isConnected;
    }

    @Override
    protected Void doInBackground(Object[] objects) {
        run();
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    // 将字节数组转为对应的16进制字符串数组
    private String byteArrayToHexString(byte[] byteArray) {
        String hexArray ="";

        for (int i = 0; i < byteArray.length; i++) {
            // 使用 Integer.toHexString 将每个字节转为16进制字符串
            hexArray =hexArray+ String.format("%02X", byteArray[i]);
        }

        return hexArray;
    }

    private boolean isSameArray(byte[] sa,byte[] ds){
        boolean isSame=false;
        if(sa.length==ds.length){
            for(int i=0;i<sa.length;i++){
                if(sa[i]!=ds[i]){
                    break;
                }
                isSame=true;
            }
        }
        return  isSame;
    }

    /**
     * 握手
     */
    public void requestShakeHands(){
        currentSendData=HptcProtocol.COM_CLIENT_SHAKE_HANDS;
        LogToFile.d(TAG, "requestShakeHands :"+byteArrayToHexString(currentSendData));
        //Log.d(TAG, "requestShakeHands :"+byteArrayToHexString(currentSendData));
        sendBytes(currentSendData);
    }
    /**
     * 开始测量
     */
    public void requestStartMeasure(){
        currentSendDataType=HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE;
        currentSendData=HptcProtocol.COM_CLIENT_MEASURE_START_DATA;
        byte[] data=hptcProtocol.pack(currentSendDataType,currentSendData);
        LogToFile.d(TAG, "requestStartMeasure :"+byteArrayToHexString(data));
        //Log.d(TAG, "requestStartMeasure :"+byteArrayToHexString(data));
        sendBytes(data);
    }
    public void requestGetDeviceStatus(){
        currentSendDataType=HptcProtocol.COM_CLIENT_PACKAGE_TYPE_DEVICE_STATUS;
        byte[] data=hptcProtocol.pack(currentSendDataType,null);
        LogToFile.d(TAG, "requestGetDeviceStatus :"+byteArrayToHexString(data));
        //Log.d(TAG, "requestGetDeviceStatus :"+byteArrayToHexString(data));
        sendBytes(data);
    }
    /**
     * 停止测量
     */
    public void requestStopMeasure(){
        currentSendDataType=HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE;
        currentSendData=HptcProtocol.COM_CLIENT_MEASURE_STOP_DATA;
        byte[] data=hptcProtocol.pack(currentSendDataType,currentSendData);
        LogToFile.d(TAG, "requestStopMeasure :"+byteArrayToHexString(data));
        //Log.d(TAG, "requestStopMeasure :"+byteArrayToHexString(data));
        sendBytes(data);
    }

    /**
     * 请求导出数据
     * @param dataAddress
     */
    public void requestExportData(int dataAddress){
        currentSendDataType=HptcProtocol.COM_CLIENT_PACKAGE_TYPE_EXPORT_DATA;
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(dataAddress);
        byte[] byteArray = buffer.array();
        byte[] currentData=new byte[5];
        currentData[0]=HptcProtocol.COM_CLINET_EXPORT_ON_DATA[0];
        currentData[1]=byteArray[0];
        currentData[2]=byteArray[1];
        currentData[3]=byteArray[2];
        currentData[4]=byteArray[3];
        byte[] data=hptcProtocol.pack(currentSendDataType,currentData);
        LogToFile.d(TAG, "requestStopMeasure :"+byteArrayToHexString(data));
        //Log.d(TAG, "requestStopMeasure :"+byteArrayToHexString(data));
        sendBytes(data);
    }

    /**
     * 停止测量
     */
    public void sendStopMeasure(){
        byte[] data=hptcProtocol.pack(HptcProtocol.COM_CLIENT_PACKAGE_TYPE_MEASURE,HptcProtocol.COM_CLIENT_MEASURE_STOP_DATA);
        LogToFile.d(TAG, "sendStopMeasure :"+byteArrayToHexString(data));
        //Log.d(TAG, "sendStopMeasure :"+byteArrayToHexString(data));
        sendBytes(data);
    }


    /**
     * 发送供电设置请求
     * @param highWidth 高电平宽度，300～256000ms
     * @param lowWidth  0电平宽度，100～256000ms
     */
    public void sendPowerSupplySetting(int highWidth,int lowWidth){
        try {
            byte[] settingValueData=hptcProtocol.transPowerSupplySettingDataToByteArray(highWidth,lowWidth);
            byte[] data=hptcProtocol.pack(HptcProtocol.COM_CLIENT_PACKAGE_TYPE_POWER_SETTING,settingValueData);
            LogToFile.d(TAG, "sendPowerSupplySetting :"+byteArrayToHexString(data));
            //Log.d(TAG, "sendPowerSupplySetting :"+byteArrayToHexString(data));
            sendBytes(data);
        } catch (Exception e) {
            LogToFile.d(TAG, "sendPowerSupplySetting exception:"+e.getMessage());
            //Log.d(TAG, "sendPowerSupplySetting exception:"+e.getMessage());
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取设备状态
     */
    public void sendGetDeviceStatus(){
        byte[] data=hptcProtocol.pack(HptcProtocol.COM_CLIENT_PACKAGE_TYPE_DEVICE_STATUS,null);
        LogToFile.d(TAG, "sendGetDeviceStatus :"+byteArrayToHexString(data));
        //Log.d(TAG, "sendGetDeviceStatus :"+byteArrayToHexString(data));
        sendBytes(data);
    }


    //byte数组转U32
    private long bytesToU32(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            throw new IllegalArgumentException("Byte array must be non-null and have a length of 4");
        }
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (bytes[i] & 0xFFL) << (8 * (3 - i));
        }
        return result;
    }
    private int bytesToU16(byte[] bytes) {
        if (bytes == null || bytes.length != 2) {
            throw new IllegalArgumentException("Byte array must be non-null and have a length of 2");
        }
        short shortValue = (short) ((bytes[0] << 8) | (bytes[1]  & 0xFF));
        int v = shortValue & 0xFFFF;
        return v;
    }
    private  String getDeviceId(byte[] bytes){
        String sn="";
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (bytes[i] & 0xFF) << (i * 8);
        }
        // 年份 提取第0位至第5位
        int  year =result & 0x1F;
        String str_year=String.valueOf(year);
        // 月份 提取第6位至第9位
        int  month =(result >> 6) & 0x0F;
        String str_month=String.valueOf(month);
        if(month<10)
            str_month="0"+str_month;
        // 设备类型 提取第10位至第14位
        int  deviceType =(result >> 10) & 0x1F;
        String str_deviceType=String.valueOf(deviceType);
        if(deviceType<10)
            str_deviceType="0"+str_deviceType;
        // 设备类型 提取第15位至第31位
        int  id =(result >> 15) & 0x7FFF;
        String str_id=String.valueOf(id);
        sn=str_year+str_month+str_deviceType+str_id;
        return  sn;
    }

    //获取电压
    private long getVoltage(byte[] bytes){
        long v=bytesToU32(bytes);
        return v;
    }
    //获取电流
    private long getCurrent(byte[] bytes){
        long v=bytesToU32(bytes);
        return v;
    }
}

