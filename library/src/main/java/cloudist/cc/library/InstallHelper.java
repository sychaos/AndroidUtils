package cloudist.cc.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by cloudist on 2017/10/26.
 */

public class InstallHelper {

    /**
     * 安装本地APK文件
     */
    public static void installLocalApk(Context appContext, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                "application/vnd.android.package-archive");
        appContext.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 安装网络APK资源
     */
    public static void installNetApk(Context appContext, String apkPath) {
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse(apkPath),
                "application/vnd.android.package-archive");
        appContext.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
