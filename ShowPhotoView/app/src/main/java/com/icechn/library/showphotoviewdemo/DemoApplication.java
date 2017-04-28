package com.icechn.library.showphotoviewdemo;

import android.app.Application;

import com.icechn.library.showphoto.ShowPhotoLib;
import com.icechn.library.showphoto.ShowPhotoLib.ShowPhotoLibParam;

/**
 * Created by ICE on 2017/2/28.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        ShowPhotoLibParam param = new ShowPhotoLibParam(getApplicationContext());
        ShowPhotoLib.init(param);
    }
}
