package cloudist.cc.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cloudist on 2017/10/26.
 */

public class BitmapUtils {

    //    dp与px的比例
    private static float dpToPxScale;

    //保存图片
    public static void saveBitmap(Bitmap bitmap, Context appContext) {
        if (Environment.getExternalStorageDirectory() == null) {
            return;
        }
        String mBaseFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";
        File file = new File(mBaseFolderPath);
        if (!file.exists()) {
            file.mkdir();
        }
        String mFilePath = mBaseFolderPath + bitmap.getGenerationId() + ".jpg";
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(mFilePath);
        } catch (FileNotFoundException e) {
            Log.d("saveBitmap", mFilePath + "FileNotFoundException");
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            stream.flush();
            ImageScannerConnection imageScannerConnection = new ImageScannerConnection(mFilePath, appContext);
            imageScannerConnection.startScan();
        } catch (Exception e) {
            Log.d("saveBitmap", e.toString());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 给bitmap加边框
     *
     * @param appContext app的context
     * @param bmp        原来的bitmap
     * @param colorRes   颜色ID
     * @param borderSize 边框大小
     * @return Bitmap
     */
    public static Bitmap addFrame(Context appContext, Bitmap bmp, int colorRes, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        //  生成画布
        Canvas canvas = new Canvas(bmpWithBorder);
        //  边框颜色
        canvas.drawColor(ContextCompat.getColor(appContext, colorRes));
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

    public int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 把一个View转化为bitmap
     *
     * @param view 待转化的View
     */
    public static Bitmap transformViewToBitmap(View view) {
        // get current view bitmap
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);
        Bitmap bmp = duplicateBitmap(bitmap);
        if (bitmap == null) {
            throw new IllegalArgumentException("can not transform this view");
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        // clear the cache
        view.setDrawingCacheEnabled(false);
        return bmp;
    }

    private static Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();
        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }
        return bmpDest;
    }

    /**
     * 获取dp/px 的值
     */
    public static float getDpToPxScale(Context context) {
        if (dpToPxScale == 0) {
            dpToPxScale = context.getResources().getDisplayMetrics().density;
        }
        return dpToPxScale;
    }

}
