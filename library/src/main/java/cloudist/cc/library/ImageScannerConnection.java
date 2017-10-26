package cloudist.cc.library;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by cloudist on 2017/10/26.
 */

public class ImageScannerConnection implements MediaScannerConnection.MediaScannerConnectionClient {

    String path = "";
    Context appContext;

    public ImageScannerConnection(String path, Context appContext) {
        this.path = path;
        this.appContext = appContext;
    }

    private static MediaScannerConnection mMs = null;

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(path, "image/jpeg");
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        mMs.disconnect();
        Toast.makeText(appContext, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public void startScan() {
        if (mMs == null) {
            mMs = new MediaScannerConnection(appContext, this);
        }
        mMs.connect();
    }
}
