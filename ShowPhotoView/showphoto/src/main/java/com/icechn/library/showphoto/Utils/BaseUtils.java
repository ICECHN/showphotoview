package com.icechn.library.showphoto.Utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;

public class BaseUtils {
    public static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    /**
     * 大图展示内部使用的view类型：webview
     */
    public static final int IMAGE_SHOW_INNER_VIEW_TYPE_WEBVIEW = 1;
    /**
     * 大图展示内部使用的view类型：fresco的draweeview
     */
    public static final int IMAGE_SHOW_INNER_VIEW_TYPE_DRAWEEVIEW = 2;


    public static boolean mkdirs(String path) {
        return new File(path).mkdirs();
    }
    /**
     *
     * @brief 获取SDCard的路径
     * @return
     * @throws
     */
    public static String getSDCardDir() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getPath();
        }
        return null;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(@NonNull Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(@NonNull Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(@NonNull Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    private static String uiBufferPath = null;
    public static void setUIBufferPath(String path) {
        uiBufferPath = path;
        if (!TextUtils.isEmpty(path)) {
            if (!uiBufferPath.endsWith(File.separator)) {
                uiBufferPath += File.separator;
            }
        } else {
            uiBufferPath = getSDCardDir() + "/uicache/";
        }
        mkdirs(uiBufferPath);
    }
    public static String getCachePathByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return uiBufferPath + url.hashCode();
    }

    /**
     * 文件是否存在着且不为空
     * @param filePath
     * @return
     */
    public static boolean isFileExistAndNotNull(String filePath){
        if (filePath == null || filePath.length() == 0){
            return false;
        }
        File file = new File(filePath);
        if (file != null && file.exists() && file.length() > 0){
            return true;
        }
        return false;
    }
}
