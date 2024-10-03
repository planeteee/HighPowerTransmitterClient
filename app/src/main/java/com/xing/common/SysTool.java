package com.xing.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SysTool {
    public static String getCurrentDatetimeString(){
        // 获取当前日期时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 将当前日期时间格式化为字符串
        String createTime = currentDateTime.format(formatter);
        return createTime;
    }
}
