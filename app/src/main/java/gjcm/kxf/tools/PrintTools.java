package gjcm.kxf.tools;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import gjcm.kxf.entity.MerchantDetail;

/**
 * Created by kxf on 2016/12/21.
 */
public class PrintTools {

    private Context context = null;
    private String deviceAddress = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    private BluetoothDevice device = null;
    private static BluetoothSocket bluetoothSocket = null;
    private static OutputStream outputStream = null;
    private static final UUID uuid = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isConnection = false;
    final String[] items = {"复位打印机", "标准ASCII字体", "压缩ASCII字体", "字体不放大",
            "宽高加倍", "取消加粗模式", "选择加粗模式", "取消倒置打印", "选择倒置打印", "取消黑白反显", "选择黑白反显",
            "取消顺时针旋转90°", "选择顺时针旋转90°"};
    final byte[][] byteCommands = {{0x1b, 0x40},// 复位打印机
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体不放大
            {0x1d, 0x21, 0x11},// 宽高加倍
            {0x1b, 0x45, 0x00},// 取消加粗模式
            {0x1b, 0x45, 0x01},// 选择加粗模式
            {0x1b, 0x7b, 0x00},// 取消倒置打印
            {0x1b, 0x7b, 0x01},// 选择倒置打印
            {0x1d, 0x42, 0x00},// 取消黑白反显
            {0x1d, 0x42, 0x01},// 选择黑白反显
            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
    };

    public PrintTools(Context context, String deviceAddress) {
        this.context = context;
        this.deviceAddress = deviceAddress;
        this.device = this.bluetoothAdapter.getRemoteDevice(this.deviceAddress);

    }

    /**
     * 获取设备名称
     *
     * @return String
     */
    public String getDeviceName() {
        return this.device.getName();
    }

    /**
     * 连接蓝牙设备
     */
    public boolean connect() {
        if (!this.isConnection) {
            try {
                bluetoothSocket = this.device
                        .createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                this.isConnection = true;
                if (this.bluetoothAdapter.isDiscovering()) {
                    System.out.println("关闭适配器！");
                    this.bluetoothAdapter.isDiscovering();
                }
            } catch (Exception e) {
                Log.i("kxflog", "连接失败！");
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 断开蓝牙设备连接
     */
    public static void disconnect() {
        System.out.println("断开蓝牙设备连接");
        try {
            bluetoothSocket.close();
            if (outputStream != null)
                outputStream.close();
            outputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (bluetoothSocket != null)
//            bluetoothSocket = null;
//        if (outputStream != null)
//            outputStream = null;
//        Log.i("kxflog", "bluetoothSocket is null:" + (null == bluetoothSocket) + "  bluetoothSocketisConnected: " + bluetoothSocket.isConnected());
//        Log.i("kxflog", "outputStream is null:" + (null == outputStream));
    }

    /**
     * 选择指令
     */
    public void selectCommand() {
        new AlertDialog.Builder(context).setTitle("请选择指令")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isConnection) {
                            try {
                                outputStream.write(byteCommands[which]);

                            } catch (IOException e) {
                                Toast.makeText(context, "设置指令失败！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "设备未连接，请重新连接！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create().show();
    }

    /**
     * 发送数据
     */
    public void send(String sendData) {
        if (this.isConnection) {
            System.out.println("开始打印！！");
            try {
                byte[] data = sendData.getBytes("gbk");
                outputStream.write(data, 0, data.length);
                outputStream.flush();
            } catch (IOException e) {
                Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(this.context, "设备未连接，请重新连接！", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void txtcom(Bitmap bitmap, String... s) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 指定调整后的宽度和高度
        int newWidth = 240;
        int newHeight = 75;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmap, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        int ww = newWidth;
        int h = newHeight;
        int w = (ww - 1) / 8 + 1;
        byte[] data = new byte[h * w + 8];
        data[0] = 0x1D;
        data[1] = 0x76;
        data[2] = 0x30;
        data[3] = 0x00;
        data[4] = (byte) w;// xL
        data[5] = (byte) (w >> 8);// xH
        data[6] = (byte) h;
        data[7] = (byte) (h >> 8);
        int k = targetBmp.getWidth() * targetBmp.getHeight();
        int[] pixels = new int[k];
        targetBmp.getPixels(pixels, 0, targetBmp.getWidth(), 0, 0, targetBmp.getWidth(), targetBmp.getHeight());
        int j = 7;
        int index = 8;
        for (int i = 0; i < pixels.length; i++) {
            int clr = pixels[i];
            int red = (clr & 0x00ff0000) >> 16;
            int green = (clr & 0x0000ff00) >> 8;
            int blue = clr & 0x000000ff;
            if (j == -1) {
                j = 7;
                index++;
            }
            data[index] = (byte) (data[index] | (RGB2Gray(red, green, blue) << j));
            j--;
        }

        String orderNumberStr = s[1];
        byte[] n1 = new byte[]{29, 104, 55};
        byte[] n2 = new byte[]{29, 119, 2};
        byte[] n3 = new byte[orderNumberStr.length()];
        int i = 0;
        while (i * 2 < orderNumberStr.length()) {
            n3[i] = Byte.valueOf(Byte.parseByte(orderNumberStr.substring(i * 2, i * 2 + 2))).byteValue();
            i += 1;
        }
        byte[] n5 = new byte[]{29, 107, 73, 14, 123, 67};
        byte[] bold = new byte[]{0x1B, 0x45, 0x01}; // 加粗
        byte[] nobold = new byte[]{0x1B, 0x45, 0x00}; // 取消加粗
        byte[] nobig = new byte[]{27, 33, 0}; //
        byte[] big = new byte[]{27, 33, 16, 1}; //0,16,32,48
        byte[] init = new byte[]{0x1B, 0x40};
        byte[] duiqi = new byte[]{0x20, 0x0A, 0x1B, 0x61, 0x00}; //  0-左对齐；1-居中对齐；2-右对齐
        byte[] youa = new byte[]{0x20, 0x0A, 0x1B, 0x61, 0x02};
        byte[] zhong = new byte[]{0x20, 0x0A, 0x1B, 0x61, 0x01};
        String blin = "------------------------------\n";
        // orderAm, orderNumberStr, note, payType, realAm, youhui, mendian, shouyy, success, paytime, undis, typeyh, shshihou);

        String temtype = s[3];
        String temtypeyh = s[11];
        String temtsjyh = s[5];//商家优惠
        String note1 = "订单总金额          " + s[0] + "\n";//8
        String note2 = "不可打折金额        " + s[10] + "\n";//4
        String note4 = "商户实收           ";
        String shanghushishou = s[12] + "\n";
        String sjyh = "商家优惠             " + temtsjyh + "\n";//商家优惠 72
        String zhifutype = "支付方式             " + temtype + "\n";// 5
        String note5 = temtype + "优惠           " + temtypeyh + "\n";// 5
        String note6 = "开票金额(用户实付)  ";
        String kaipiaojine = s[4] + "\n";
        String note10 = "退款码\n";
        String note7 = "商户订单号 \n" + s[1] + "\n";
        String note8 = "商户名称   " + s[6] + "\n";
        String note11 = "操作员     " + s[7] + "\n";
        String note9 = "支付时间 " + s[9] + "\n";
        try {
            buffer = new ByteArrayOutputStream();
            byte[] bytelin = blin.getBytes("gbk");
            byte[] byte1 = note1.getBytes("gbk");
            byte[] byte2 = note2.getBytes("gbk");
            byte[] byte4 = note4.getBytes("gbk");
            byte[] byte5 = note5.getBytes("gbk");
            byte[] byte6 = note6.getBytes("gbk");
            byte[] byte7 = note7.getBytes("gbk");
            byte[] byte8 = note8.getBytes("gbk");
            byte[] byte9 = note9.getBytes("gbk");
            buffer.write(init);
            buffer.write(zhong);
            buffer.write(data);
            buffer.write(duiqi);
            buffer.write(bytelin);
            buffer.write(byte1);
            buffer.write(byte2);
            buffer.write(big);
            buffer.write(byte4);
            buffer.write(bold);
            buffer.write(shanghushishou.getBytes("gbk"));
            buffer.write(nobig);
            buffer.write(nobold);
            buffer.write(bytelin);
            buffer.write(zhifutype.getBytes("gbk"));
            if (!temtsjyh.equals("0.00")) {
                buffer.write(sjyh.getBytes("gbk"));
            }
            if (!temtypeyh.equals("0.00")) {
                buffer.write(byte5);
            }
            buffer.write(big);
            buffer.write(byte6);
            buffer.write(bold);
            buffer.write(kaipiaojine.getBytes("gbk"));
            buffer.write(nobig);
            buffer.write(nobold);
            buffer.write(bytelin);
            buffer.write(byte7);
            buffer.write(note10.getBytes("gbk"));
            buffer.write(n1);
            buffer.write(n2);
            buffer.write(n5);
            buffer.write(n3);
            buffer.write(byte8);
            buffer.write(note11.getBytes("gbk"));
            buffer.write(bytelin);
            buffer.write(byte9);
            buffer.write(bytelin);
            outputStream.write(buffer.toByteArray());
            outputStream.flush();
            disconnect();
//            outputStream.write(buffer.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte RGB2Gray(int r, int g, int b) {
        return (int) (0.29900 * r + 0.58700 * g + 0.11400 * b) < 150 ? (byte) 1 : (byte) 0;
    }

    //String paramString1, String paramString2, String paramString3, String paramString4, String paramString5
    public void printRefoundMonery(String mendian, String shouyy, String orderNumberStr, String orderAm, String success, String ztuikuan, String youhui, String paytime) {
        if (this.isConnection) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] left = ESCUtil.alignLeft();
                buffer.write(left);
                ztuikuan = "退款金额：" + ztuikuan + "\n";
                orderNumberStr = "订单编号：\n" + orderNumberStr + "\n";
                orderAm = "订单金额：" + orderAm + "\n";
                mendian = "门店名：" + mendian + "\n";
                String fuk = "********** 退款凭证 **********\n\n";
                shouyy = "收银员：" + shouyy + "\n";
                success = "退款状态：" + success + "\n";
                paytime = "支付时间：" + paytime + "\n\n";
//                undis = "不参加优惠金额：" + undis + "\n";
                buffer.write(fuk.getBytes("gbk"));
                buffer.write(mendian.getBytes("gbk"));
                buffer.write(shouyy.getBytes("gbk"));
                buffer.write(orderNumberStr.getBytes("gbk"));
                buffer.write(success.getBytes("gbk"));
                buffer.write(orderAm.getBytes("gbk"));
                if ("0.00".equals(youhui) || "0.0".equals(youhui) || "0".equals(youhui)) {
                } else {
                    youhui = "优惠金额：" + youhui + "\n";
                    buffer.write(youhui.getBytes("gbk"));
                }
                buffer.write(ztuikuan.getBytes("gbk"));
                buffer.write(paytime.getBytes("gbk"));
                String note = "\n";
                buffer.write(note.getBytes("gbk"));
                outputStream.write(buffer.toByteArray());
                outputStream.flush();
                disconnect();
            } catch (Exception paramString1) {
                Toast.makeText(this.context, "发送失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this.context, "蓝牙未连接，请重新连接", Toast.LENGTH_SHORT).show();
    }

    public void printDeatail(String orderAm, String orderNumberStr, String note, String payType, String realAm, String youhui, String mendian, String shouyy, String success, String paytime, String undis, String typeyh, String shanghushishou, String tuiam) {
        if (this.isConnection) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                byte[] left = ESCUtil.alignLeft();
                buffer.write(left);
                String s1 = payType;
                payType = "支付方式：" + payType + "\n";
                realAm = "用户实付：" + realAm + "\n";
                String orderNumberStrtw = "商家订单号：\n" + orderNumberStr + "\n";
                orderAm = "订单金额：" + orderAm + "\n";
                mendian = "门店名：" + mendian + "\n";
                String fuk = "********** 订单凭证 **********\n\n";
                shouyy = "操作员：" + shouyy + "\n";
                success = "支付状态：" + success + "\n";
                note = "备注：" + note + "\n";
                paytime = "支付时间：" + paytime + "\n";
                buffer.write(fuk.getBytes("gbk"));
                buffer.write(mendian.getBytes("gbk"));
                buffer.write(shouyy.getBytes("gbk"));
                buffer.write(orderNumberStrtw.getBytes("gbk"));
                buffer.write(success.getBytes("gbk"));
                buffer.write(payType.getBytes("gbk"));
                buffer.write(orderAm.getBytes("gbk"));
                if ("0.00".equals(youhui) || "0.0".equals(youhui) || "0".equals(youhui)) {
                } else {
                    youhui = "商家优惠：" + youhui + "\n";
                    buffer.write(youhui.getBytes("gbk"));
                }
                if ("0.00".equals(typeyh) || "0.0".equals(typeyh) || "0".equals(typeyh)) {
                } else {
                    s1 = s1 + "优惠：" + typeyh + "\n";
                    buffer.write(s1.getBytes("gbk"));
                }
//                buffer.write(youhui.getBytes("gbk"));
                shanghushishou = "商户实收：" + shanghushishou + "\n";
                buffer.write(shanghushishou.getBytes("gbk"));
                buffer.write(realAm.getBytes("gbk"));
                if (!tuiam.equals("0")) {
                    String bufne = "部分退款金额：" + tuiam + "\n";
                    buffer.write(bufne.getBytes("gbk"));
                }
                if ("0.00".equals(undis) || "0.0".equals(undis) || "0".equals(undis)) {
                } else {
                    undis = "不打折金额：" + undis + "\n";
                    buffer.write(undis.getBytes("gbk"));
                }
                buffer.write(paytime.getBytes("gbk"));
                buffer.write(note.getBytes("gbk"));
                String str = "退款码";
                buffer.write(str.getBytes("gbk"));
                buffer.write(new byte[]{29, 104, 55});
                buffer.write(new byte[]{29, 119, 2});
                byte[] heheh = new byte[orderNumberStr.length()];
                int i = 0;
                while (i * 2 < orderNumberStr.length()) {
                    heheh[i] = Byte.valueOf(Byte.parseByte(orderNumberStr.substring(i * 2, i * 2 + 2))).byteValue();
                    i += 1;
                }
                byte[] paramString1 = new byte[3];
                buffer.write(paramString1);
                buffer.write(new byte[]{29, 107, 73, 14, 123, 67});
                buffer.write(heheh);
//                buffer.write(paramString2);
                note = "\n\n\n";
                buffer.write(note.getBytes("gbk"));
                outputStream.write(buffer.toByteArray());
                outputStream.flush();
                disconnect();
            } catch (Exception paramString1) {
                Toast.makeText(this.context, "打印数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this.context, "蓝牙未连接，请重新连接", Toast.LENGTH_SHORT).show();
    }

    //扫码打印
    public void printScandPay(Object... s) {
        if (connect()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] initprint = new byte[]{27, 64};//初始化
            byte[] leftcenter = new byte[]{27, 97, 0};//靠左
            byte[] newline = new byte[]{10};//换行
            String title = "********** 扫码订单 **********\n";
            try {
                byte[] store = ("门店名:" + s[0] + "\n").getBytes("gbk");
                byte[] ordernum = ("订单号:" + s[1] + "\n").getBytes("gbk");
                byte[] orderway = ("支付方式:" + s[2] + "\n").getBytes("gbk");
                byte[] orederam = ("订单金额:" + s[3] + "\n").getBytes("gbk");
                byte[] orderreal = ("用户实付:" + s[4] + "\n").getBytes("gbk");
                byte[] merchantreal = ("商户实收:" + s[5] + "\n").getBytes("gbk");
                byte[] paystatus = ("支付状态:" + s[6] + "\n").getBytes("gbk");
                byte[] paytime = ("支付时间:" + s[7] + "\n").getBytes("gbk");
                buffer.write(initprint);
                buffer.write(leftcenter);
                buffer.write(title.getBytes("gbk"));
                buffer.write(store);
                buffer.write(ordernum);
                buffer.write(orderway);
                buffer.write(orederam);
                buffer.write(orderreal);
                buffer.write(merchantreal);
                buffer.write(paystatus);
                double merchantw = (double) s[8];
                if (merchantw > 0.00) {
                    byte[] merchanyh = ("商家优惠:" + merchantw + "\n").getBytes("gbk");
                    buffer.write(merchanyh);
                }
                double paytw = (double) s[9];
                if (paytw > 0.00) {
                    byte[] payyh = (s[2] + "优惠:" + paytw + "\n").getBytes("gbk");
                    buffer.write(payyh);
                }
                buffer.write(paytime);
                outputStream.write(buffer.toByteArray());
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            disconnect();
        }
    }

    public void printScale(String mendian, String dianyuan, String totalAmount, String totalOrderCount, String refundAmount, String realPayAmount, String discountAmount, String merchantTotalAmount, String cardTotalAmount, String refundCount, String serviceAmount, String beginTime, String endTime,
                           String wxPaySum, String wxRefundSum, String wxPayCount, String AliPaySum, String AliPayRefundSum, String aliPayCount) {
        if (isConnection) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] initprint = new byte[]{27, 64};//初始化
                byte[] leftcenter = new byte[]{27, 97, 0};//靠左
                byte[] newline = new byte[]{10};//换行
                totalAmount = "总金额:" + totalAmount;
                mendian = "门店:" + mendian;
                dianyuan = "操作员:" + dianyuan;
                totalOrderCount = "订单笔数:" + totalOrderCount;
                refundAmount = "退款金额:" + refundAmount;
                realPayAmount = "顾客实付:" + realPayAmount;
                discountAmount = "优惠金额:" + discountAmount;
                merchantTotalAmount = "商家实收:" + merchantTotalAmount;
                cardTotalAmount = "卡消费金额:" + cardTotalAmount;
                refundCount = "退款笔数:" + refundCount;
                AliPaySum = "支付宝支付金额:" + AliPaySum+"\n";
                AliPayRefundSum = "支付宝退款金额:" + AliPayRefundSum+"\n";
                aliPayCount = "支付宝支付笔数:" + aliPayCount+"\n";
                wxPaySum = "微信支付金额:" + wxPaySum+"\n";
                wxRefundSum = "微信退款金额:" + wxRefundSum+"\n";
                wxPayCount = "微信支付笔数:" + wxPayCount+"\n";
                beginTime = "开始时间:" + beginTime;
                endTime = "结束时间:" + endTime + "\n\n";
                String title = "**********   结算  **********\n";
                buffer.write(initprint);
                buffer.write(title.getBytes("gbk"));
                buffer.write(leftcenter);
                buffer.write(mendian.getBytes("gbk"));
                buffer.write(newline);
                buffer.write(dianyuan.getBytes("gbk"));
                buffer.write(newline);//////顺序
                buffer.write(totalOrderCount.getBytes("gbk"));///订单数
                buffer.write(newline);
                buffer.write(totalAmount.getBytes("gbk"));///总金额
                buffer.write(newline);
                buffer.write(merchantTotalAmount.getBytes("gbk"));///商家实收
                buffer.write(newline);
                buffer.write(realPayAmount.getBytes("gbk"));
                buffer.write(newline);
                buffer.write(discountAmount.getBytes("gbk"));
                buffer.write(newline);
                buffer.write(refundCount.getBytes("gbk"));
                buffer.write(newline);
                buffer.write(refundAmount.getBytes("gbk"));///
                buffer.write(newline);
                buffer.write(cardTotalAmount.getBytes("gbk"));
//                buffer.write(newline);
//                buffer.write(serviceAmount.getBytes("gbk"));
                buffer.write(newline);
                buffer.write(AliPaySum.getBytes("gbk"));
                buffer.write(aliPayCount.getBytes("gbk"));
                buffer.write(AliPayRefundSum.getBytes("gbk"));
                buffer.write(wxPaySum.getBytes("gbk"));
                buffer.write(wxPayCount.getBytes("gbk"));
                buffer.write(wxRefundSum.getBytes("gbk"));
                buffer.write(beginTime.getBytes("gbk"));
                buffer.write(newline);
                buffer.write(endTime.getBytes("gbk"));
                outputStream.write(buffer.toByteArray());
                outputStream.flush();
                disconnect();
            } catch (Exception paramString1) {
                Toast.makeText(this.context, "打印数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this.context, "蓝牙未连接，请重新连接", Toast.LENGTH_SHORT).show();

    }


    public void printList(List<MerchantDetail> merchantDetails) {
        if (isConnection) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] initprint = new byte[]{27, 64};//初始化
            byte[] leftcenter = new byte[]{27, 97, 0};//靠左
            byte[] newline = new byte[]{10};//换行
            String title = "---------结算订单详情-------" + "\n";
            try {
                buffer.write(title.getBytes("gbk"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < merchantDetails.size(); i++) {
                MerchantDetail merchantDetail = merchantDetails.get(i);
                String ordernumber = "订单号:" + merchantDetail.getOrderNumber() + "\n";
                String orderamount = "订单金额:" + merchantDetail.getOrderAmount() + "\n";
                String paytime = "支付时间:" + merchantDetail.getPayTime() + "\n";
                String status = "支付状态:" + merchantDetail.getStatus() + "\n";
                String type = "支付类型:" + merchantDetail.getType() + "\n";
                String refount = merchantDetail.getRefoundAmount();
                try {
                    buffer.write(ordernumber.getBytes("gbk"));
                    buffer.write(orderamount.getBytes("gbk"));
                    buffer.write(status.getBytes("gbk"));
                    buffer.write(type.getBytes("gbk"));
                    if (!("0").equals(refount)) {
                        refount = "退款金额:" + merchantDetail.getRefoundAmount() + "\n";
                        buffer.write(refount.getBytes("gbk"));
                    }
                    buffer.write(paytime.getBytes("gbk"));
                    buffer.write("-----------------------------".getBytes("gbk"));
                    buffer.write(newline);
                    String str = "\n";
                    buffer.write(str.getBytes("gbk"));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                outputStream.write(buffer.toByteArray());
                outputStream.flush();
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this.context, "蓝牙未连接，请重新连接", Toast.LENGTH_SHORT).show();
        }
    }
}
