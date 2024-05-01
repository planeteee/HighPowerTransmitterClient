package com.xing.net;



public class HptcProtocol {


    //包Id的最大值，即2位byte的最大值
    private static final int MAX_PACKAGE_ID = 65535;
    //协议版本号
    private  static final byte[] COM_PROTOCOL_VERSION = {(byte)0x01,(byte)0x00};


    /**
     * 握手请求
     */
    public static final byte[] COM_SHAKE_HANDS_REQUEST = {(byte)0xc3,(byte)0xc3,(byte)0xc3,(byte)0xc3};;

    /**
     * 握手成功
     */
    public static final byte[] COM_SERVER_SHAKE_HANDS_SUCCESS = {(byte)0xC6,(byte)0xC6,(byte)0xC6,(byte)0xC6};
    /**
     * 服务器主动断开
     */
    public static final byte[] COM_SERVER_DISCONNECT= {(byte)0xA3,(byte)0xA3,(byte)0xA3,(byte)0xA3};
    /**
     * 握手应答  为大功率大功率发射机发射机
     */
    private static final byte[] COM_SHAKE_HANDS_RESPONSE_HPTC = {(byte)0xC6,(byte)0xC6,(byte)0xC6,(byte)0xC6};

    /**
     * 请求头
     */
    private static final byte[] COM_CLIENT_HEAD = {(byte)0xff,(byte)0x1f,(byte)0x2f,(byte)0x3f};
    /**
     * 应答头
     */
    private static final byte[] COM_SERVER_HEAD ={(byte)0xee,(byte)0xe4,(byte)0xe5,(byte)0xe6};
    /**
     * 0x0000	请求处理成功
     * 0x0001	协议版本错误
     * 0x0002	数据包校验错误
     * 0x0003	请求设备操作失败
     * 0x0004	资源错误
     * 0x0005	繁忙
     * 0x0006	请求包类型错误
     * 0x0007	请求的数据异常
     * 0x0008	文件CRC异常
     * 0x0009	指定的文件不存在
     * 0xffff	未知原因出错
     */
    /**
     * 请求处理成功
     */
    private static final byte[] COM_SERVER_RESULT_OK = {(byte)0x00,(byte)0x00};
    /**
     * 协议版本错误
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_VERSION = {(byte)0x00,(byte)0x01};
    /**
     * 数据包校验错误
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_PACKAGE_CRC = {(byte)0x00,(byte)0x02};
    /**
     * 请求设备操作失败
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_OPERATION = {(byte)0x00,(byte)0x03};
    /**
     * 资源错误
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_RESOURCE = {(byte)0x00,(byte)0x04};
    /**
     * 繁忙
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_BUSY = {(byte)0x00,(byte)0x05};
    /**
     * 请求包类型错误
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_PACKAGE_TYPE = {(byte)0x00,(byte)0x06};
    /**
     * 请求的数据异常
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_DATA_EXCEPTION = {(byte)0x00,(byte)0x07};
    /**
     * 文件CRC异常
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_FILE_CRC = {(byte)0x00,(byte)0x08};
    /**
     * 指定的文件不存在
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_FILE_NOT_EXIST = {(byte)0x00,(byte)0x09};
    /**
     * 未知原因出错
     */
    private static final byte[] COM_SERVER_RESULT_ERROR_UNKNOWN = {(byte)0xFF,(byte)0xFF};

    //客户端发送的
    /**
     * 握手
     */
    public static final byte[] COM_CLIENT_SHAKE_HANDS = {(byte)0xC3,(byte)0xC3,(byte)0xC3,(byte)0xC3};
    /**
     * 请求断开连接
     */
    private static final byte[] COM_CLIENT_DISCONNECT =  {(byte)0xA3,(byte)0xA3,(byte)0xA3,(byte)0xA3};
    /**
     * 包类型 开始测量
     */
    public static final byte[] COM_CLIENT_PACKAGE_TYPE_MEASURE = {(byte)0x00,(byte)0x01};
    /**
     * 包类型 供电设置
     */
    public static final byte[] COM_CLIENT_PACKAGE_TYPE_POWER_SETTING = {(byte)0x00,(byte)0x03};
    /**
     * 包类型 获取设备状态
     */
    public static final byte[] COM_CLIENT_PACKAGE_TYPE_DEVICE_STATUS = {(byte)0x00,(byte)0x05};
    /**
     * 包类型 导出数据
     */
    public static final byte[] COM_CLIENT_PACKAGE_TYPE_EXPORT_DATA = {(byte)0x00,(byte)0x0C};
    /**
     *  请求 开始测量 数据
     */
    public static final byte[] COM_CLIENT_MEASURE_START_DATA = {(byte)0x01};
    /**
     *  请求 停止测量  数据
     */
    public static final byte[] COM_CLIENT_MEASURE_STOP_DATA = {(byte)0x00};
    public static final byte[] COM_CLINET_EXPORT_ON_DATA= {(byte)0x01};
    public static final byte[] COM_CLINET_EXPORT_OFF_DATA= {(byte)0x00};



    //当前的包ID，第一个id从0开始
    private int currentPackageId=0;
    //当前包ID的byte类型，用于解包
    private byte[] currentPackageIdByte;

    /**
     * 请求动作
     */
    public enum DeviceAction{
        /**
         * 未知动作
         */
        UNKNOWN_ACTION,
        /**
         * 握手
         */
        SHAKE_HANDS,
        /**
         * 开始测量
         */
        MEASURE_START,
        /**
         * 停止测量
         */
        MEASURE_STOP,
        /**
         * 供电设置
         */
        POWER_SETTINGS,
        /**
         * 请求设备状态
         */
        REQUEST_STATUS,
        /**
         * 请求设备参数
         */
        REQEUST_PARAMS,
        /**
         * 断开连接
         */
        DISCONNECTED,
        /**
         * 连接设备失败
         */
        CONNECT_FAIL,

    }

    /**
     * 回传给UI的消息类型
     */
    public enum MessageType{
        /**
         * 动作执行成功
         */
        ACTION_OK,
        /**
         * 动作执行失败
         */
        ACTION_FAIL,
        /**
         * 错误
         */
        ERROR,
    }
    /**
     * 通信状态，包括网络状态和请求处理的状态
     */
    public enum CommunicateStatus {

        /**
         * 握手成功
         */
        SHAKE_HANDS_OK,
        /**
         * 握手失败
         */
        SHAKE_HANDS_FAIL,
        /**
         * 解析成功，没有错误
         */
        OK_UNPACK,
        /**
         * 解析时，响应头不匹配
         */
        ERROR_UNPACK_HEAD_NOT_MATCHED,
        /**
         *解析时，校验不匹配
         */
        ERROR_UNPACK_CRC_NOT_MATCHED,
        /**
         *解析时，协议版本不匹配
         */
        ERROR_UNPACK_VERSION_NOT_MATCHED,

        /**
         *根据响应结果判断，协议版本不匹配
         */
        ERROR_RESULT_VERSION_NOT_MATCHED,
        /**
         * 根据响应结果，发送数据的校验和不匹配
         */
        ERROR_RESULT_PACKAGE_NOT_MATCHED,
        /**
         * 根据响应结果，请求设备操作失败
         */
        ERROR_RESULT_DEVICE_OPERATION,
        /**
         * 根据响应结果，资源错误
         */
        ERROR_RESULT_RESOURCE,
        /**
         * 根据响应结果，繁忙
         */
        ERROR_RESULT_BUSY,
        /**
         * 根据响应结果，请求包类型错误
         */
        ERROR_RESULT_PACKAGE_TYPE,
        /**
         * 根据响应结果，请求的数据异常
         */
        ERROR_RESULT_DATA_EXCEPTION,
        /**
         * 根据响应结果，文件CRC异常
         */
        ERROR_RESULT_FILE_CRC,
        /**
         * 根据响应结果，指定的文件不存在
         */
        ERROR_RESULT_FILE_NOT_EXIST,
        /**
         * 根据响应结果，未知原因出错
         */
        ERROR_RESULT_UNKNOWN
    }
    //接收到的数据
    public class ReceivedData{
        public int packageId;
        public byte[] dataType;
        public byte[] data;
        public CommunicateStatus status;
    }
    public void shakeHands(){

    }
    /**
     * 打包要发送的数据
     * data可以为null
     */
    public byte[] pack(byte[] packageType,byte[] data){
        try{
            int dateLen=0;
            if(data!=null){
                dateLen=data.length;
            }
            byte[] pack=new byte[12+dateLen];
            byte[] packWithCrc=new byte[14+dateLen];
            currentPackageIdByte=getPackageIdByteArray();
            byte[] len=int2ByteArray2(dateLen);
            for(int i=0;i<pack.length;i++){
                if(i<4){
                    pack[i]=COM_CLIENT_HEAD[i];
                }else if(i>=4 && i<6){
                    pack[i]=packageType[i-4];
                }else if(i>=6 && i<8){
                    pack[i]=COM_PROTOCOL_VERSION[i-6];
                }else if(i>=8 && i<10){
                    pack[i]=currentPackageIdByte[i-8];
                }else if(i>=10 && i<12){
                    pack[i]=len[i-10];
                }else if(i>=12 && i<pack.length){
                    if(data!=null)
                        pack[i]=data[i-12];
                }
            }
            //byte[] crc=makefcs(pack);
            byte[] crc=lowSum(pack);
            for (int  i=0;i<packWithCrc.length;i++){
                if(i<packWithCrc.length-2){
                    packWithCrc[i]=pack[i];
                }else {
                    packWithCrc[i]=crc[i-(packWithCrc.length-2)];
                }
            }
            return packWithCrc;
        }catch (Exception e){
            throw e;
        }

    }

    /**
     *  解包接收的数据
     */
    public ReceivedData unpack(byte[] data){
        ReceivedData rd=null;
        if(data.length>=16){
            rd=new ReceivedData();
            byte[] unpackHead=new byte[4];
            byte[] unpackType=new byte[2];
            byte[] unpackVersion=new byte[2];
            byte[] unpackPackageId=new byte[2];
            byte[] unpackResult=new byte[2];
            byte[] unpackDataLength=new byte[2];
            int dataLength=0;//
            byte[] unpackData=null;
            if(dataLength>0){
                unpackData=new byte[data.length-16];
            }
            byte[] unpackCRC=new byte[2];
            for(int i=0;i<data.length;i++){
                if(i<4){
                    unpackHead[i]=data[i];
                }else if(i>=4 && i<6){
                    unpackType[i-4]=data[i];
                }else if(i>=6 && i<8){
                    unpackVersion[i-6]=data[i];
                }else if(i>=8 && i<10){
                    unpackPackageId[i-8]=data[i];
                }else if(i>=10 && i<12){
                    unpackResult[i-10]=data[i];
                }else if(i>=12 && i<14){
                    unpackDataLength[i-12]=data[i];
                    if(i==13){
                        dataLength= ((unpackDataLength[0] & 0xFF) << 8) | (unpackDataLength[1] & 0xFF);
                        if(dataLength>0){
                            unpackData=new byte[data.length-16];
                        }else {
                            unpackData=null;
                        }
                    }
                }
                if(i>=14){
                    if(dataLength>0){
                        if(i<data.length-2){
                            unpackData[i-14]=data[i];
                        }else if(i>=data.length-2){
                            unpackCRC[i-(data.length-2)]=data[i];
                        }
                    }else {
                        //没有数据载荷的时候
                        unpackData=null;
                        unpackCRC[i-(data.length-2)]=data[i];
                    }

                }
            }
            if(!isSameArray(unpackHead,COM_SERVER_HEAD)){
                rd.status= CommunicateStatus.ERROR_UNPACK_HEAD_NOT_MATCHED;
            }else {
                if(!isSameArray(unpackVersion,COM_PROTOCOL_VERSION)){
                    rd.status= CommunicateStatus.ERROR_UNPACK_VERSION_NOT_MATCHED;
                }else {
                    byte[] dataWithOutCRC=new byte[data.length-2];
                    for(int i=0;i<dataWithOutCRC.length;i++){
                        dataWithOutCRC[i]=data[i];
                    }
                    //byte[] crcCalc=makefcs(dataWithOutCRC);
                    byte[] crcCalc=lowSum(dataWithOutCRC);

                    if(!isSameArray(unpackCRC,crcCalc)){
                        rd.status= CommunicateStatus.ERROR_UNPACK_CRC_NOT_MATCHED;
                    }else {
                        if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_VERSION)){
                            rd.status= CommunicateStatus.ERROR_RESULT_VERSION_NOT_MATCHED;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_PACKAGE_CRC)){
                            rd.status= CommunicateStatus.ERROR_RESULT_PACKAGE_NOT_MATCHED;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_OPERATION)){
                            rd.status= CommunicateStatus.ERROR_RESULT_DEVICE_OPERATION;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_RESOURCE)){
                            rd.status= CommunicateStatus.ERROR_RESULT_RESOURCE;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_BUSY)){
                            rd.status= CommunicateStatus.ERROR_RESULT_BUSY;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_PACKAGE_TYPE)){
                            rd.status= CommunicateStatus.ERROR_RESULT_PACKAGE_TYPE;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_DATA_EXCEPTION)){
                            rd.status= CommunicateStatus.ERROR_RESULT_DATA_EXCEPTION;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_FILE_CRC)){
                            rd.status= CommunicateStatus.ERROR_RESULT_FILE_CRC;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_FILE_NOT_EXIST)){
                            rd.status= CommunicateStatus.ERROR_RESULT_FILE_NOT_EXIST;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_ERROR_UNKNOWN)){
                            rd.status= CommunicateStatus.ERROR_RESULT_UNKNOWN;
                        }else if(isSameArray(unpackResult,COM_SERVER_RESULT_OK)){
                            rd.status= CommunicateStatus.OK_UNPACK;
                            rd.data=unpackData;
                            rd.dataType=unpackType;
                            rd.packageId= ((unpackPackageId[0] & 0xFF) << 8) | (unpackPackageId[1] & 0xFF);
                        }
                    }
                }
            }
        }
        return rd;
    }

    /**
     * 将包id转为2为byte
     */
    private byte[] getPackageIdByteArray(){
        if(currentPackageId<MAX_PACKAGE_ID){
            currentPackageId++;
        }else {
            //到达最大时，又从头开始
            currentPackageId=0;
        }
        byte[] bytes = int2ByteArray2(currentPackageId);
        return bytes;
    }

    /**
     * 将int转为2位byte
     * @param number
     * @return
     */
    private byte[] int2ByteArray2(int number){
        byte[] byteArray = new byte[2];

        // 使用位运算将整数转换为2个字节的byte数组
        byteArray[0] = (byte) (number >> 8); // 高位在前
        byteArray[1] = (byte) number;        // 低位在后

        return byteArray;
    }
    /**
     * 将int转为4位byte
     * @param number
     * @return
     */
    private byte[] int2ByteArray4(int number){
        byte[] byteArray = new byte[4];

        // 使用位运算将整数转换为 4 个字节的 byte 数组
        byteArray[0] = (byte) (number >> 24);
        byteArray[1] = (byte) (number >> 16);
        byteArray[2] = (byte) (number >> 8);
        byteArray[3] = (byte) number;

        return byteArray;
    }
    /**
     * CRC校验和
     */
    private byte[] makefcs(byte[] data)
    {
        int crc=0xFFFF;
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        byte[] bup=new byte[2];
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];  //数据的复制
        }
        int len = buf.length;
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; //^异或:用于位运算，每个位相同为0，不同为1

            } else {
                crc ^= (int) buf[pos];
            }
            for (int i = 8; i != 0; i--) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;   //右移运算符
                    crc ^= 0xA001;
                } else
                    crc >>= 1;
            }
        }
        String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
        }
        bup[0]=(byte)(Integer.parseInt(c.substring(0, 1), 16)+Integer.parseInt(c.substring(1,2), 16));
        bup[1]=(byte)(Integer.parseInt(c.substring(2, 3), 16)+Integer.parseInt(c.substring(3,4), 16));
        return bup;
    }

    /**
     * 低4位求和，存为2为byte数组
     * @param data
     * @return
     */
    private byte[] lowSum(byte[] data){
        int sum = 0;
        for (byte b : data) {
            int lower4Bits = b & 0x0F; // 取低4位
            sum += lower4Bits;
        }
        // 将求和结果存为2位的新字节数组
        byte[] resultByteArray = {(byte) ((sum >> 4) & 0x0F), (byte) (sum & 0x0F)};
        return resultByteArray;
    }

    /**
     * 判断2个数组值是否相等
     */
    private boolean isSameArray(byte[] sa,byte[] ds){
        boolean isSame=false;
        if(sa.length==ds.length){
            for(int i=0;i<sa.length;i++){
                if(sa[i]!=ds[i]){
                    isSame=false;
                    break;
                }
                isSame=true;
            }
        }
        return  isSame;
    }

    /**
     * 将供电设置的数据载荷转为byte
     * @param highWidth 高电平宽度，300～256000ms
     * @param lowWidth  0电平宽度，100～256000ms
     * @return
     */
    public byte[] transPowerSupplySettingDataToByteArray(int highWidth,int lowWidth) throws Exception {
        if(highWidth<300 || highWidth>256000 || lowWidth<100 || lowWidth>256000){
            throw new Exception("高电平宽度，300～256000ms,0电平宽度，100～256000ms");
        }
        byte[] highWidthData=int2ByteArray4(highWidth);
        byte[] lowWidthData=int2ByteArray4(lowWidth);
        byte[] data=new byte[12];
        for(int i=0;i<data.length;i++){
            if(i<3){
                data[i]=(byte)0x00;
            }else if(i==3){
                data[i]=(byte)0x01;
            }else if(i>=4 && i<8) {
                data[i]=highWidthData[i-4];
            }else {
                data[i]=lowWidthData[i-8];
            }
        }
        return  data;
    }
}
