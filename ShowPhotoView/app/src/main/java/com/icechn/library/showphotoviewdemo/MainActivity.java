package com.icechn.library.showphotoviewdemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import com.icechn.library.showphoto.Utils.BaseUtils;
import com.icechn.library.showphoto.Photo.MultiPhotoDialog;
import com.icechn.library.showphoto.Photo.PhotoInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        findViewById(R.id.btn_showimage_draweeview).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(true);
            }
        });
        findViewById(R.id.btn_showimage_webview).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(false);
            }
        });
    }

    private void showImage(boolean isDraweeview) {
        String[] urls = new String[]{
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489242738821&di=1566cde95d77f2aae3adccba278e089b&imgtype=0&src=http%3A%2F%2Fimgq.duitang.com%2Fuploads%2Fitem%2F201404%2F02%2F20140402182945_Yyar4.thumb.700_0.jpeg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489242738821&di=f3e77010e5a4f9553307a54812100a60&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01445b56f1ef176ac7257d207ce87d.jpg%40900w_1l_2o_100sh.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489242891443&di=1a80b5755a05222e59847e82277712ce&imgtype=0&src=http%3A%2F%2Fupload.art.ifeng.com%2F2015%2F0811%2F1439260959533.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489242891443&di=745b802e44ebadcf52b8358f90c00d74&imgtype=0&src=http%3A%2F%2Fbizhi.zhuoku.com%2F2009%2F04%2F07%2Fshuidi%2Fweiju14.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489242987595&di=c9f88c9fca8b43defc904278c31c3c72&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fnature%2FAmazing_Color_Landscape_2560x1600%2Fwallpapers%2F1920x1200%2FAmazing_Landscape_24_II.jpg"
        };
        ArrayList<PhotoInfo> list = new ArrayList<PhotoInfo>();
        for (int i = 0; i < urls.length; i++) {
            PhotoInfo info = new PhotoInfo();
            info.url = urls[i];
            list.add(info);
        }
        new MultiPhotoDialog(MainActivity.this,
                isDraweeview? BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_DRAWEEVIEW:BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_WEBVIEW)
                .show(list);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
