package cloudist.cc.library;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by cloudist on 2017/10/26.
 */

public class AppAwake {

    public static void openOtherApp(String packName, Context appContext) {
        if (packName == null) {
            throw new IllegalArgumentException(packName + "can not null");
        }
        PackageManager packageManager = appContext.getPackageManager();
        if (checkAppExist(packName, appContext)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packName);
            appContext.startActivity(intent);
        } else {
            Toast.makeText(appContext, "没有安装" + packName, Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean checkAppExist(String packName, Context appContext) {
        PackageManager packageManager = appContext.getPackageManager();
        try {
            packageManager.getPackageInfo(packName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
