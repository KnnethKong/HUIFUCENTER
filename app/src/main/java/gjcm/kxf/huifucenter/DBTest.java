package gjcm.kxf.huifucenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import gjcm.kxf.drawview.CarouselView;
import gjcm.kxf.getui.GeTuiIntentService;
import gjcm.kxf.getui.GetTuiServer;
import gjcm.kxf.tools.PrintSoundPR;

/**
 * Created by kxf on 2017/3/15.
 */
public class DBTest extends AppCompatActivity {
    private ContentResolver contentResolver;
    TextView msg;
    private int[] mImagesSrc = {
            R.drawable.b1,
            R.drawable.b2,
            R.drawable.b3,
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_huifu);
        CarouselView carouselView= (CarouselView) findViewById(R.id.main_fragment_carousel);
        carouselView.setAdapter(new CarouselView.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(mImagesSrc[position]);
                return imageView;
            }

            @Override
            public int getCount() {
                return mImagesSrc.length;
            }
        });

//        PushManager.getInstance().initialize(this.getApplicationContext(), GetTuiServer.class);
//        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GeTuiIntentService.class);
//        contentResolver = getContentResolver();
//        msg = (TextView) findViewById(R.id.dbtest_msg);

    }

    public void dbAdd(View view) {
        EditText editText = (EditText) findViewById(R.id.dbtest_edit);
        String s = editText.getText().toString();
        if (s == null)
            s = "is null";
        ContentValues printvalues = new ContentValues();
        printvalues.put(PrintSoundPR.KEY_CONTENT, false);
        printvalues.put(PrintSoundPR.KEY_ID, 12545);
        printvalues.put(PrintSoundPR.KEY_DEL, 1);
        ContentValues soundvalues = new ContentValues();
        soundvalues.put(PrintSoundPR.KEY_CONTENT, false);
        soundvalues.put(PrintSoundPR.KEY_ID, 12546);
        soundvalues.put(PrintSoundPR.KEY_DEL, 1);
        Uri printurl = contentResolver.insert(PrintSoundPR.CONTENT_URI, printvalues);
        Uri soundurl = contentResolver.insert(PrintSoundPR.CONTENT_URI, soundvalues);
        msg.setText("newUri" + soundurl + "\n" + printurl);
    }

    public void dbUpdate(View view) {
        ContentValues values = new ContentValues();
        values.put(PrintSoundPR.KEY_CONTENT, true);
        Uri uri = Uri.parse(PrintSoundPR.CONTENT_URI_STRING + "/" + 12545);
        int result = contentResolver.update(uri, values, null, null);
        String smsg = "更新ID的数据12545" + (result > 0 ? "成功" : "失败");
        values.put(PrintSoundPR.KEY_CONTENT, true);
        uri = Uri.parse(PrintSoundPR.CONTENT_URI_STRING + "/" + 12546);
        result = contentResolver.update(uri, values, null, null);
        smsg += "更新ID的数据12545" + (result > 0 ? "成功" : "失败");
        msg.setText(smsg);

    }

    public void dbQuery(View view) {
        msg.setText("");
        Uri uri = Uri.parse(PrintSoundPR.CONTENT_URI_STRING + "/" + 12545);
        Cursor cursor = contentResolver.query(uri, new String[]{PrintSoundPR.KEY_ID, PrintSoundPR.KEY_CONTENT, PrintSoundPR.KEY_DEL}, null, null, null);
        StringBuffer stringBuffer = new StringBuffer();
        if (cursor.moveToFirst()) {
//            do {
                stringBuffer.append("  ID: " + cursor.getString(cursor.getColumnIndex(PrintSoundPR.KEY_ID)));
                stringBuffer.append("  KEY_CONTENT: " + cursor.getString(cursor.getColumnIndex(PrintSoundPR.KEY_CONTENT)));
                stringBuffer.append("  KEY_DEL: " + cursor.getInt(cursor.getColumnIndex(PrintSoundPR.KEY_DEL)) + "\n");
//            } while (cursor.moveToNext());
        }
        cursor.close();
        msg.setText(stringBuffer.toString());
    }

}
