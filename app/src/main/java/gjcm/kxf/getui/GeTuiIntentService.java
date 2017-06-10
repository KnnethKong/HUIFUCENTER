package gjcm.kxf.getui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import org.json.JSONException;
import org.json.JSONObject;

import gjcm.kxf.tools.PrintSoundPR;
import gjcm.kxf.tools.PrintTools;

/**
 * Created by kxf on 2016/12/23.
 */
public class GeTuiIntentService extends GTIntentService {
    String TAG = "kxflog";
    private SpeechSynthesizer mTts;
    private String notimsg = "", path;

    public GeTuiIntentService() {
//        Log.d(TAG, "DemoIntentService    -> GeTuiIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTts = SpeechSynthesizer.createSynthesizer(getApplicationContext(), null);
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        path = sharedPreferences.getString("sdpath", "");
    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "GeTuiIntentService    -> onReceiveServicePid" + pid);
    }


    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        byte[] payload = msg.getPayload();
        String s = new String(payload);
        Log.e(TAG, "GeTuiIntentService    -> onReceiveMessageData  print:"+ s);
        try {
            JSONObject payloadJson = new JSONObject(s);
            boolean isscancode = payloadJson.getBoolean("isscancode");
            if (isscancode) {
                String sound = payloadJson.getString("soundmsg");
                notimsg = sound;
                palySond(sound);
            } else {
                boolean isSound = payloadJson.getBoolean("isSound");
                if (isSound) {
                    JSONObject soundJson = payloadJson.getJSONObject("soundJson");
                    String sound = soundJson.getString("soundMsg");
                    notimsg = sound;
                    palySond(sound);
                }
                boolean isPrint = payloadJson.getBoolean("isPrint");
                if (isPrint){
                    JSONObject printJson = payloadJson.getJSONObject("printJson");
                    PrintTools printTools = new PrintTools(context, "00:11:22:33:44:55");
                    String payType = printJson.getString("payType");
                    String storeName = printJson.getString("storeName");
                    double orderAmount = printJson.getDouble("orderAmount");
                    String payTime = printJson.getString("payTime");
                    String orderNumber = printJson.getString("orderNumber");
                    String payStatus = printJson.getString("payStatus");
                    double realPay = printJson.getDouble("realPay");
                    double merchantCheques = printJson.getDouble("merchantCheques");
                    double merchantDiscount = printJson.getDouble("merchantDiscount");
                    double payDiscount = printJson.getDouble("payDiscount");
                    Object[] objects = new Object[]{storeName, orderNumber, payType, orderAmount, realPay, merchantCheques, payStatus, payTime, merchantDiscount, payDiscount};
                    printTools.printScandPay(objects);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void palySond(final String data) {

        new Thread() {
            @Override
            public void run() {
                mTts.setParameter(SpeechConstant.PARAMS, null);
                mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
                mTts.setParameter(SpeechConstant.SPEED, "50");//音速
                mTts.setParameter(SpeechConstant.PITCH, "50");//设置合成音调
                mTts.setParameter(SpeechConstant.VOLUME, "80");//合成音量
                //设置播放器音频流类型
                mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
                // 设置播放合成音频打断音乐播放，默认为true
                mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
                mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                path = path + "/woshi.wav";
                mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, path);
                int code = mTts.synthesizeToUri(data, path, mTtsListener);
//                    Log.e("kxflog", "path: " + path + "code:" + code);
                if (code != ErrorCode.SUCCESS) {
                    Log.e("kxflog", "语音合成失败,错误码: " + code);
                }
            }

        }.start();
    }

    public SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            mTts.stopSpeaking();
            mTts.destroy();
            mTts = null;
            Intent showintent = new Intent(getApplicationContext(), ShowNotificationReceiver.class);
            showintent.putExtra("title", "收款提醒");
            showintent.putExtra("msg", notimsg);
            showintent.putExtra("sound", path);
            getApplicationContext().sendBroadcast(showintent);

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    @Override
    public void onReceiveClientId(Context context, String clientid) {
//        Log.i(TAG, "DemoIntentService----->   onReceiveClientId " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.i(TAG, "DemoIntentService----->   onReceiveOnlineState " + "online = " + online);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {

        Log.i(TAG, "DemoIntentService----->   onReceiveCommandResult " + "cmdMessage = " + cmdMessage.getAction() + cmdMessage.getPkgName());
    }
}
