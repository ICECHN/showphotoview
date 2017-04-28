package com.icechn.library.showphotoviewdemo;

import android.app.Application;

import com.icechn.library.showphoto.ShowPhotoLib;
import com.icechn.library.showphoto.ShowPhotoLib.ShowPhotoLibParam;


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
