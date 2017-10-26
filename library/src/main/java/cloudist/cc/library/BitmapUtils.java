package cloudist.cc.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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

    /**
     * 获取目标文件的size
     *
     * @param file 目标文件
     * @return size 单位byte
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            int size2 = 0;
            if (fileList != null) {
                size2 = fileList.length;
                for (int i = 0; i < size2; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
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
