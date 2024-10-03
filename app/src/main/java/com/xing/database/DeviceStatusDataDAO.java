package com.xing.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xing.common.SysTool;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatusDataDAO {
    private final SQLiteDatabase db;

    public DeviceStatusDataDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // 插入学生
    public long insert(DeviceStatusData deviceStatusData) {
        ContentValues values = new ContentValues();
        values.put(DeviceStatusData.COLUMN_DEVICE_ID, deviceStatusData.getDeviceId());
        values.put(DeviceStatusData.COLUMN_VOLTAGE, deviceStatusData.getVoltage());
        values.put(DeviceStatusData.COLUMN_CURRENT, deviceStatusData.getCurrent());
        values.put(DeviceStatusData.COLUMN_TEMP, deviceStatusData.getTemperature());
        values.put(DeviceStatusData.COLUMN_BATTERY, deviceStatusData.getBattery());
        values.put(DeviceStatusData.COLUMN_POWER, deviceStatusData.getPower());
        values.put(DeviceStatusData.COLUMN_GPS, deviceStatusData.getGps());
        String createTime = SysTool.getCurrentDatetimeString();
        values.put(DeviceStatusData.COLUMN_CREATE_TIME, createTime);
        return db.insert(DeviceStatusData.TABLE_NAME, null, values);
    }

    // 查询所有
    public List<DeviceStatusData> getAll() {
        int id=-1 ;
        String deviceId="" ;
        String voltage="" ;
        String current="" ;
        String temperature="" ;
        String battery="" ;
        String power="" ;
        String gps="" ;
        String createTime="" ;
        List<DeviceStatusData> deviceStatusDatas = new ArrayList<>();
        Cursor cursor = db.query(DeviceStatusData.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int idIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_ID);
                if(idIndex>-1){
                    id = cursor.getInt(idIndex);
                }
                int deviceIdIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_DEVICE_ID);
                if(deviceIdIndex>-1){
                    deviceId = cursor.getString(deviceIdIndex);
                }
                int voltageIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_VOLTAGE);
                if(voltageIndex>-1){
                    voltage = cursor.getString(voltageIndex);
                }
                int currentIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_CURRENT);
                if(currentIndex>-1){
                    current = cursor.getString(currentIndex);
                }
                int temperatureIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_TEMP);
                if(temperatureIndex>-1){
                    temperature = cursor.getString(temperatureIndex);
                }
                int batteryIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_BATTERY);
                if(batteryIndex>-1){
                    battery = cursor.getString(batteryIndex);
                }
                int powerIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_POWER);
                if(powerIndex>-1){
                    power = cursor.getString(powerIndex);
                }
                int gpsIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_GPS);
                if(gpsIndex>-1){
                    gps = cursor.getString(gpsIndex);
                }
                int createTimeIndex=cursor.getColumnIndex(DeviceStatusData.COLUMN_CREATE_TIME);
                if(createTimeIndex>-1){
                    createTime = cursor.getString(createTimeIndex);
                }
                deviceStatusDatas.add(new DeviceStatusData(id, deviceId, voltage,current,temperature,battery,power,gps,createTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return deviceStatusDatas;
    }

    // 根据ID删除
    public int delete(int id) {
        return db.delete(DeviceStatusData.TABLE_NAME, DeviceStatusData.COLUMN_ID+" = ?", new String[]{String.valueOf(id)});
    }

    // 更新信息
    public int update(DeviceStatusData deviceStatusData) {
        ContentValues values = new ContentValues();
        values.put(DeviceStatusData.COLUMN_DEVICE_ID, deviceStatusData.getDeviceId());
        values.put(DeviceStatusData.COLUMN_VOLTAGE, deviceStatusData.getVoltage());
        values.put(DeviceStatusData.COLUMN_CURRENT, deviceStatusData.getCurrent());
        values.put(DeviceStatusData.COLUMN_TEMP, deviceStatusData.getTemperature());
        values.put(DeviceStatusData.COLUMN_BATTERY, deviceStatusData.getBattery());
        values.put(DeviceStatusData.COLUMN_POWER, deviceStatusData.getPower());
        values.put(DeviceStatusData.COLUMN_GPS, deviceStatusData.getGps());
        String createTime = SysTool.getCurrentDatetimeString();
        values.put(DeviceStatusData.COLUMN_CREATE_TIME, createTime);
        return db.update(DeviceStatusData.TABLE_NAME, values, DeviceStatusData.COLUMN_ID+" = ?", new String[]{String.valueOf(deviceStatusData.getId())});
    }
}
