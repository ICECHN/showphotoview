package com.icechn.library.showphoto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.icechn.library.showphoto.Utils.BaseUtils;
import com.icechn.library.showphoto.Utils.FrescoHelper;


public class ShowPhotoLib {
    public static void init(@NonNull ShowPhotoLibParam param) {
        String path = param.fileBufferPath;
        if (TextUtils.isEmpty(path)) {
            path = BaseUtils.getSDCardDir() + "/frescoCache/";
            Log.w("showphoto", "frescoBufferPath is empty, using default value:" + path);
        }
        FrescoHelper.initFresco(param.context, path, null);
        BaseUtils.setUIBufferPath(param.fileBufferPath);
    }
    public static class ShowPhotoLibParam {
        public ShowPhotoLibParam(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }
        Context context = null;
        public String fileBufferPath = null;
    }
}
