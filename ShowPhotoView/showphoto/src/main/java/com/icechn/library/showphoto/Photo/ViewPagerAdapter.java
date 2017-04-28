package com.icechn.library.showphoto.Photo;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewPagerAdapter extends PagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();
    private static final boolean LOGV = true;//false;
    
    /** 
     * 获取视图，用于设置对应位置的pager内容
     * @param container
     * @param position
     * @return
     */
    abstract public View getView(ViewGroup container, int position);
    
    /**
     * 删除对应位置pager的视图
     * 如果需要资源回收（如Bitmap），可在这里面实现
     * @param container
     * @param position
     * @param view
     */
    abstract public void destroyView(ViewGroup container, int position, View view);
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(container, position);
                
        container.addView(view);
    
        if (LOGV) {
            Log.v(TAG, "init position = " + position + ", child count =" + container.getChildCount());
        }
        
        return view;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        
        destroyView(container, position, (View) object);
        
        if (LOGV) {
            Log.v(TAG, "destroy position = " + position  + ", child count =" + container.getChildCount());
        }
    }

    @Override
    final public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    final public int getItemPosition(Object object) {
        if (LOGV) {
            Log.v(TAG, "getItemPosition");
        }
        return POSITION_NONE;
    }
}
