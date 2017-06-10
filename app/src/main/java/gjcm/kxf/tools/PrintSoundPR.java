package gjcm.kxf.tools;

import android.net.Uri;

/**
 * Created by kxf on 2017/3/16.
 */
public class PrintSoundPR {
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
    public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
    public static final String MIME_ITEM = "vnd.example.printsound";

    public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM;
    public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + "/" + MIME_ITEM;

    public static final String AUTHORITY = "com.gjcm.allprovide";
    public static final String PATH_SINGLE = "printsound/#";
    public static final String PATH_MULTIPLE = "printsound";
    public static final String CONTENT_URI_STRING = "content://" + AUTHORITY + "/" + PATH_MULTIPLE;
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_DEL = "del";
    public static final String KEY_TITLE = "titles";
//    public static final String KEY_AGE = "age";
//    public static final String KEY_HEIGHT = "height";
}
