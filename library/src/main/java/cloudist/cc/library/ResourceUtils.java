package cloudist.cc.library;

import java.io.File;

/**
 * Created by cloudist on 2017/10/30.
 */

public class ResourceUtils {

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
}
