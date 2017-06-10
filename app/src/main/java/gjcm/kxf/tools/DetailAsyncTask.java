package gjcm.kxf.tools;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by kxf on 2016/12/17.
 */
public class DetailAsyncTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        String token = strings[1];
        String tradeno = strings[2];
        try {
            String json = null;
            try {
                OtherTools otherTools = new OtherTools();
                 json = otherTools.getDetail(url,token, tradeno);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return json;
        } catch (Exception e) {
            Log.e("kxflog", e.toString());
            return null;
        }
    }


    @Override
    protected void onPostExecute(String json) {
        if (json != null && json != "") {
            taskHandler.taskSuccessful(json);
        } else {
            taskHandler.taskFailed();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public interface HttpTaskHandler {
        void taskSuccessful(String json);

        void taskFailed();
    }

    HttpTaskHandler taskHandler;

    public void setTaskHandler(HttpTaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }
}
