package gjcm.kxf.tools;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by kxf on 2016/11/29.
 */
public class MyAsyncTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        String token = strings[1];
        int pageNO = Integer.parseInt(strings[2]);
        int everyPageCount = Integer.parseInt(strings[3]);
        String payEndTime = strings[4];
        String payStartTime = strings[5];
        String payStatus = strings[6];
        try {
            String json = null;
            try {
                json = new NetTools().requestByPost(url, token,pageNO,everyPageCount,payEndTime,payStartTime,payStatus);
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
