package gjcm.kxf.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kxf on 2016/11/28.
 */
public class NetTools {
        public static String HOMEURL = "http://merchant.vikpay.com:9050";
//  public static String HOSTURL = "http://dc.vikpay.com/HuiFuOrder/";
 //   public static String HOMEURL = "http://weijing.f3322.net:9090/";
      public static String HOSTURL = "http://weijing.f3322.net:7070/HuiFuOrder/";
//        public static String HOSTURL = "http://192.168.1.3:7070/UmengPushServer/";
    //public static String HOMEURL = "http://huifu.vikpay.com";
   //public static String HOMEURL = "http://192.168.1.6:8080";
//    public static String HOMEURL = "http://client.vikpay.com";
    private String TAG = "kxflog";
    private StringBuffer sbuffer;
    public String requestByPost(String path, String token, int pageNO, int everyPageCount, String payEndTime, String payStartTime,String status) throws Throwable {
        String params = "{" +
                "\"dto\":{" + "\"payStartTime\":" + "\"" + payStartTime + "\"" + ",\"payEndTime\":" + "\"" + payEndTime + "\"" + ",\"status\":" + status + "}," +
                "\"page\":{" + "\"pageNO\":" + pageNO + ",\"everyPageCount\":" + everyPageCount + "}" +
                "}";
        byte[] postData = params.getBytes();
        URL url = new URL(path);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(5000);
        urlConn.setReadTimeout(5000);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
//        urlConn.setChunkedStreamingMode(0);
        urlConn.setInstanceFollowRedirects(true);
        urlConn.setRequestProperty("Content-Type",
                "application/json");
        urlConn.setRequestProperty("token", token);
//        Log.e("kxflog", "params:" + params+"  token:"+token+"   path:"+path);
        urlConn.connect();
        DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
        dos.write(postData);
        dos.flush();
        dos.close();
        Log.i("kxflog","urlConn.getResponseCode():"+urlConn.getResponseCode());
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
            Log.i(TAG, "Post方式请求失败");
        }
        return sbuffer.toString();
    }







    private String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            System.out.print("length:" + byteArray.toString());
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 判断WIFI网络是否可用
     * @param context
     * @return
     */
    private boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 判断MOBILE网络是否可用
     * @param context
     * @return
     */
    private static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 获取当前网络连接的类型信息
     * @param context
     * @return
     */
    private static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }


    /**
     * 获取当前的网络状态 ：没有网络0：WIFI网络1：3G网络2：2G网络3
     * @param context
     * @return
     */
    private static int getAPNType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;// wifi
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager mTelephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    && !mTelephony.isNetworkRoaming()) {
                netType = 2;// 3G
            } else {
                netType = 3;// 2G
            }
        }
        return netType;
    }

}
