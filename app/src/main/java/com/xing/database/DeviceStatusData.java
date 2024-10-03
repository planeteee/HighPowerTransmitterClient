package com.xing.database;

public class DeviceStatusData  {
    // 表名和列名
    public static final String TABLE_NAME = "device_status_data_table";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DEVICE_ID = "device_id";
    public static final String COLUMN_VOLTAGE = "voltage";
    public static final String COLUMN_CURRENT = "current";
    public static final String COLUMN_TEMP = "temperature";
    public static final String COLUMN_BATTERY = "battery";
    public static final String COLUMN_POWER = "power";
    public static final String COLUMN_GPS = "gps";
    public static final String COLUMN_CREATE_TIME = "createTime";

    private int id ;
    private String deviceId ;
    private String voltage ;
    private String current ;
    private String temperature ;
    private String battery ;
    private String power ;
    private String gps ;
    private String createTime;


    public DeviceStatusData(int id,String deviceId,String voltage,String current,String temperature,String battery,String power,String gps,String createTime){
        this.id=id;
        this.deviceId=deviceId;
        this.voltage=voltage;
        this.current=current;
        this.temperature=temperature;
        this.battery=battery;
        this.power=power;
        this.gps=gps;
        this.createTime =createTime;
    }
    public DeviceStatusData(String deviceId,String voltage,String current,String temperature,String battery,String power,String gps,String createTime){
        this.deviceId=deviceId;
        this.voltage=voltage;
        this.current=current;
        this.temperature=temperature;
        this.battery=battery;
        this.power=power;
        this.gps=gps;
        this.createTime =createTime;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
