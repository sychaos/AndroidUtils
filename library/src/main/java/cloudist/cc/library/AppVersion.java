package cloudist.cc.library;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by cloudist on 2017/10/26.
 */

public class AppVersion {

    public static final int FORCE_UPDATE = 1000;
    public static final int OPTIONAL_UPDATE = 1001;
    public static final int NO_UPDATE = 1002;

    public static String getAppName(int pID, Context appContext) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            //注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verName;
    }

    public static int checkNewNewVersion(String latestVersion, String lastCompatibleVersion, Context appContext) {
        String[] latest = latestVersion.split("\\.");
        String[] lastCompatible = lastCompatibleVersion.split("\\.");
        String[] now = getVerName(appContext).split("\\.");
        for (int i = 0; i < Math.max(lastCompatible.length, now.length); i++) {//与最小版本逐位比较
            int lastCompatibleCode = (i >= lastCompatible.length) ? 0 : Integer.valueOf(lastCompatible[i]);
            int nowCode = (i >= now.length) ? 0 : Integer.valueOf(now[i].split("\\s+")[0]);//debug包会自动在版本号后加“ Debug”,过滤掉

            if (lastCompatibleCode > nowCode) {
                return FORCE_UPDATE; //强制更新
            } else if (lastCompatibleCode < nowCode) {
                break;
            }
        }

        for (int i = 0; i < Math.max(latest.length, now.length); i++) {//与最新版本逐位比较
            int latestCode = (i >= latest.length) ? 0 : Integer.valueOf(latest[i]);
            int nowCode = (i >= now.length) ? 0 : Integer.valueOf(now[i].split("\\s+")[0]);

            if (latestCode > nowCode) {
                return OPTIONAL_UPDATE;
            } else if (latestCode < nowCode) {
                break;
            }
        }

        return NO_UPDATE;
    }
}
