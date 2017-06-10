package gjcm.kxf.tools;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/4.
 */
public class OtherTools {
    public static String HOMEURL = "http://client.vikpay.com";
    public static boolean isprint = false, issound = false;


    //    public static String zhixingurl = "http://api.open.cater.meituan.com/tuangou/coupon/consume";
//    public static String bendiurl = "http://api.open.cater.meituan.com/tuangou/coupon/queryLocalListByDate";
//    public static String couponQuery = "http://api.open.cater.meituan.com/tuangou/coupon/queryById";
//    public static String zhunbeiurl = "http://api.open.cater.meituan.com/tuangou/coupon/prepare";
//    public static String chexiao = "http://api.open.cater.meituan.com/tuangou/coupon/cancel";
//    public static String mendianyanquanlishi = " http://api.open.cater.meituan.com/tuangou/coupon/queryListByDate";
    private void getInfo(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
    }

    public static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    private void getScreen(Context context, Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String str = sdf.format(date);
        return str;
    }

    public static int getMemory() {
        return 1;

    }

    //详情
    public String getDetail(String strurl, String usertoken, String trandeno) throws Throwable {
        String params = "orderNumber=" + trandeno;
        String newStr = new String(params.getBytes(), "UTF-8");
        byte[] postData = newStr.getBytes();
        URL url = new URL(NetTools.HOMEURL + strurl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(5 * 1000);
        urlConn.setReadTimeout(5 * 1000);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        urlConn.setInstanceFollowRedirects(true);
        urlConn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");
        urlConn.setRequestProperty("token", usertoken);
        urlConn.connect();
        DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
        dos.write(postData);
//        System.out.print("Post方式请" + params + "  getHost:" + url.getHost() + url.getPort() + "  " + strurl + "  usertoken:" + usertoken);
        Log.i("kxflog", "Post方式请求失败" + params + "  url-" + strurl + "  usertoken:" + usertoken);
//        System.out.print("postData:" +new String(postData));
        dos.flush();
        dos.close();
        StringBuffer sbuffer = null;
        if (urlConn.getResponseCode() == 200) {
            InputStreamReader inputStream = new InputStreamReader(urlConn.getInputStream());
            BufferedReader reader = new BufferedReader(inputStream);
            String lines;
            sbuffer = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbuffer.append(lines);
            }
            reader.close();
        } else {
//            Log.i("kxflog", "Post方式请求失败");
        }
        System.out.print("postData:" + urlConn.getResponseCode());
        return sbuffer.toString();
    }

    //退款
    public String tuikuanByPost(String path, String token, String id, String pwd, String free) throws Throwable {
        StringBuffer sbuffer = null;
        String params = "id=" + id + "&password=" + pwd + "&refundFee=" + free;
        byte[] postData = params.getBytes();
        URL url = new URL(path);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(15 * 1000);
        urlConn.setReadTimeout(15 * 1000);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        urlConn.setInstanceFollowRedirects(true);
        urlConn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");
        urlConn.setRequestProperty("token", token);
        Log.e("kxflog", "params:" + params + "  token:" + token + "   path:" + path + "-------------------");
        urlConn.connect();
        DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
        dos.write(postData);
        dos.flush();
        dos.close();
        if (urlConn.getResponseCode() == 200) {
            InputStreamReader inputStream = new InputStreamReader(urlConn.getInputStream());//调用HttpURLConnection连接对象的getInputStream()函数, 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            BufferedReader reader = new BufferedReader(inputStream);
            String lines;
            sbuffer = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbuffer.append(lines);

            }
//            System.out.print("" + sbuffer.toString());
//            Log.e("kxflog", "" + sbuffer.toString());
            reader.close();
        } else {
            Log.i("kxflog", "Post方式请求失败" + urlConn.getResponseCode());
        }
        return sbuffer.toString();
    }


    public String sendGet(String url, String charset) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {

       /* Pattern p = Pattern
                .compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");
        Matcher m = p.matcher("eaEEW");
        System.out.print("eaEEW--->" + m.matches());
        m = p.matcher("eaaaEEW111");
        System.out.print("eaaaEEW111--->" + m.matches());
        m = p.matcher("1121142");
        System.out.print("1121142--->" + m.matches());
        m = p.matcher("12s2ds2");
        System.out.print("12s2ds2--->" + m.matches());
        m = p.matcher("12s2ds2sddas1s5");
        System.out.print("12s2ds2--->" + m.matches());*/

        try {
            JSONArray jsonArray = new
                    JSONArray();
            JSONObject jsonObject = new JSONObject("sss");
            jsonObject.put("int", 12);
            jsonObject.put("string", "我的");
            jsonObject.put("isuc", true);
            jsonArray.put(jsonObject);
            System.out.print("jsonArray--->" + jsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Calendar now = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//       String str= sdf.format(now.getTimeInMillis());
//        System.out.print("" + str);
//        Calendar now=Calendar.getInstance();
//        now.add(Calendar.MINUTE,30);
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateStr=sdf.format(now.getTimeInMillis());
//        System.out.println(dateStr);
//        try {
//            long curren = System.currentTimeMillis();
//            curren += 15 * 60 * 1000;
//            Date da = new Date(curren);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String str = dateFormat.format(da);
//            da = dateFormat.parse(str);
//            long unixTimestamp = da.getTime();
//            System.out.print("" + unixTimestamp);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        try {
//            String url = "http://client.vikpay.com/manager-order/info";
//            String token = "f7d360d246ee90678332343a47bfc339";
//            String tradeno = "201612161006250425878310";
//            String pwd = "shayan2";
        OtherTools otherTools = new OtherTools();
//            otherTools.tuikuanByPost(url, token, tradeno, pwd);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
        DecimalFormat localDecimalFormat = new DecimalFormat("#.##");

        DecimalFormat df3 = new DecimalFormat("0.00");
        String number = "123";
        Double cny = Double.parseDouble(number);//转换成Double
        System.out.print(df3.format(cny));
        System.out.print(localDecimalFormat.format(cny));
//        otherTools.sub(number);
//        number = "1.";
//        otherTools.sub(number);
//        number = "2.2";
//        otherTools.sub(number);
//        number = "1.23";
//        otherTools.sub(number);
//        try {
//            OtherTools otherTools = new OtherTools();
//            String str = otherTools.getDetail("/order/app/store-info", "a6fb6bcc42a800e6325f741e5e8fd15b", "201612281431303991132980" );//201612241654037026360630  201612211755457324821540
////            String str = otherTools.getDetail("a6fb6bcc42a800e6325f741e5e8fd15b", "201612241654037026360630");
//            System.out.print(str);
////            otherTools.tuikuanByPost("http://client.vikpay.com/pay/wx-pay-refund-self-store", "f7d360d246ee90678332343a47bfc339", "", "");
////            otherTools.getDetail("201612161753580264951080");
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }

    }

    public static void sub(String number) {
        if (number.contains(".")) {
            String[] strg = number.split("\\.");
            if (strg.length > 1) {
                int nowl = strg[1].length();
                if (nowl > 2)
                    strg[1].substring(0, 2);
                System.out.println(strg[0] + "." + strg[1]);
            } else
                System.out.println(number);

        }

    }


}
