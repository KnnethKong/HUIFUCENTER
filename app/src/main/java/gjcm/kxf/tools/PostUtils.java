package gjcm.kxf.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kxf on 2016/12/21.
 */
public class PostUtils {
    private StringBuffer sbuffer;

    public String requestByPost(String path, String token,String payStartTime,String payEndTime,int storeid,int storeuserid) throws Throwable {
//int pageNO, int everyPageCount, String payEndTime, String payStartTime

//        orderNumber=&startTime=2016-12-17%2021%3A40%3A53&endTime=2016-12-20%2021%3A40%3A53&status=0&page.pageNO=1&page.everyPageCount=20

        String params = "{" +//"storeid":0,"storeuserid":0
                "\"dto\":{"  + "\"payEndTime\":" + "\"" + payEndTime + "\""+ ",\"payStartTime\":" + "\"" + payStartTime + "\"" + ",\"realname\":" + null +
                ",\"status\":" + -1 + ",\"storeid\":" + storeid + ",\"storeuserid\":" + storeuserid + "}," +
                "\"page\":{" +  "\"everyPageCount\":" + 1000+ ",\"pageNO\":" + 1 +"}" +
                "}";
        byte[] postData = params.getBytes();
        URL url = new URL(path);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(10 * 1000);
        urlConn.setReadTimeout(65*1000);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        urlConn.setInstanceFollowRedirects(true);
        urlConn.setRequestProperty("Content-Type",
                "application/json");
        urlConn.setRequestProperty("token", token);
        System.out.println(params+" + params+"  +token+"+token");
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
            System.out.print("respons-" + sbuffer.toString());
            reader.close();
        } else {
            System.out.print("Post方式请求失败");
        }
        return sbuffer.toString();
    }

//
//    public static void main(String[] args) {
//        String url = "http://client.vikpay.com/order/app/store-search";
////        url="http://client.vikpay.com/order/app/store-info";
//        String token = "81b64b34534555d39fcccb1c617c9543";
//        PostUtils p = new PostUtils();
//        try {
//            p.requestByPost(url, token, 1, 2, null, null);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//
//    }


    public static void main(String[] args) throws Exception {

        PostUtils postUtils = new PostUtils();
        try {
            postUtils.requestByPost("http://ts15898070.51mypc.cn:9090/testSSM/manager-order/searchCount", "3c7ebe4dcbc3e7f299f62c33c582a5d6","2016-12-20 00:00", "2016-12-30 23:59",4050,4463);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

       /* DecimalFormat df3 = new DecimalFormat("0.00");

        String s= df3.parse("0.02").toString();
        System.out.println(s);


        Date dd = new Date();
        Date date = new Date(dd.getTime() - 3 * 24 * 60 * 60 * 1000);

        System.out.println(date.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long curren = System.currentTimeMillis();
        Date da = new Date(curren);
        String str = dateFormat.format(da);
        System.out.println(str);
        curren -= 3 * 24 * 60*60 * 1000;
        da = new Date(curren);
         str = dateFormat.format(da);
        System.out.println(str);


      str=   str.substring(0,str.length()-2);

        System.out.println(str);*/
    }
}
