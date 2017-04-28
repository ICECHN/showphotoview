package com.icechn.library.showphoto.Photo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.icechn.library.showphoto.R;
import com.icechn.library.showphoto.Utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ICE on 2016/12/23.
 */

public class MultiPhotoDialog extends Dialog {
    protected FrameLayout mMainLayout = null;
    protected TextView mIndexView = null;
    protected ViewPager mViewPager = null;
    protected List<PhotoInfo> mInfos = new ArrayList<PhotoInfo>();
    protected int mPhotoViewType = BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_DRAWEEVIEW;

    public MultiPhotoDialog(@NonNull Context context) {
        super(context, R.style.Show_Image_DialogTheme);
        initContentView(context);
    }
    public MultiPhotoDialog(@NonNull Context context, int type) {
        super(context, R.style.Show_Image_DialogTheme);
        if (type != BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_WEBVIEW) {
            mPhotoViewType = BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_DRAWEEVIEW;
        } else {
            mPhotoViewType = BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_WEBVIEW;
        }
        initContentView(context);
    }

    private void initContentView(Context context) {
        mMainLayout = new FrameLayout(context);

        mViewPager = new MultiTouchViewPager(context);
        mViewPager.setAdapter((new GalleryAdapter()));
        mViewPager.setOffscreenPageLimit(1);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mMainLayout.addView(mViewPager, params);
        mIndexView = new TextView(context);
        mIndexView.setTextColor(Color.WHITE);
        mIndexView.setShadowLayer(2, 2, 2, Color.rgb(40, 40, 40));
        mIndexView.setGravity(Gravity.CENTER);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                BaseUtils.dp2px(context, 60));
        mMainLayout.addView(mIndexView, params);

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndexView.setText((position + 1) + "/" + mViewPager.getAdapter().getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setContentView(mMainLayout);
    }

    protected int getPhotoViewType() {
        return mPhotoViewType;
    }

    public void show(ArrayList<PhotoInfo> list) {
        show(list, 0);
    }

    /**
     *
     * @param list
     * @param selectedIndex 当前显示第几张
     */
    public void show(ArrayList<PhotoInfo> list, int selectedIndex) {
        if (list == null || list.size() == 0) {
            Toast.makeText(getContext(), "图片列表为空", Toast.LENGTH_SHORT).show();
            return ;
        }
        mIndexView.setText("");
        mInfos.clear();
        mInfos.addAll(list);
        mViewPager.getAdapter().notifyDataSetChanged();
        if (mInfos.size() > 0 && selectedIndex >= 0 && selectedIndex < mInfos.size()) {
            mViewPager.setCurrentItem(selectedIndex);
            mIndexView.setText((selectedIndex + 1) + "/" + mInfos.size());
        } else {
            mViewPager.setCurrentItem(0);
            mIndexView.setText( "1/" + mInfos.size());
        }
        show();
    }

    private View.OnClickListener mClickImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide();
        }
    };
    private PhotoInfo getImageInfo(int index) {
        if (index < 0 || mInfos.size() - 1 < index) {
            return null;
        }
        return mInfos.get(index);
    }

    private View getPhotoView(Context context, PhotoInfo info) {
        PhotoBase view;
        switch (getPhotoViewType()) {
            case BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_WEBVIEW:
                view = new WebPhotoView(context);
                break;
            case BaseUtils.IMAGE_SHOW_INNER_VIEW_TYPE_DRAWEEVIEW:
            default:
                view = new DraweePhotoView(context);
        }
        view.setClickImageListener(mClickImageListener);
        if (info != null) {
            view.show(info);
        }
        return view;
    }
    private final class GalleryAdapter extends ViewPagerAdapter {

        @Override
        public View getView(ViewGroup container, int position) {
            PhotoInfo info = getImageInfo(position);
            return getPhotoView(getContext(), info);
        }

        @Override
        public void destroyView(ViewGroup container, int position, View view) {
            if (view == null) {
                return;
            }
            if (view instanceof DraweePhotoView) {
                ((DraweePhotoView)view).destroy();
            }
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }
    }
}
