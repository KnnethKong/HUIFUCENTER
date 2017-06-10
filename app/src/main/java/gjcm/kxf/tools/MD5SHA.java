package gjcm.kxf.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Administrator on 2016/11/3.
 */
public class MD5SHA {

    private static String md5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    private static String addData() {
        Map<String, String> maps = new TreeMap<>();
        maps.put("timestamp", "1470364890606");
        maps.put("appAuthToken", "abc");
        maps.put("charset", "UTF-8");
        maps.put("couponCode", "012345678900");
        Set<String> keySet = maps.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuffer sbf = new StringBuffer();
        while (iter.hasNext()) {
            String key = iter.next();
            sbf.append(key + maps.get(key));
        }
        System.out.println("" + sbf);
        String shaStr = sha1(sbf.toString());
        System.out.println("sha1:" + shaStr);
        shaStr = shaStr.toLowerCase();
        return shaStr;
    }

    private static String sha1(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        DecimalFormat df = new DecimalFormat("0.00");//格式化
           double d=255580.362;
       String st= df.format(d);
        System.out.println("sigin:" + st);

//        String data = "timestamp=1470364890606&appAuthToken= abc&charset=UTF-8&couponCode=012345678900";
//        //MD5
//        System.out.println("MD5 : " + md5(data));
//        //SHA1
//        System.out.println("SHA1 : " + sha1(data));
    }
}
