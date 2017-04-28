package com.icechn.library.showphoto.Photo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icechn.library.showphoto.Progress.CircleProgress;
import com.icechn.library.showphoto.R;
import com.icechn.library.showphoto.Utils.BaseUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class PhotoBase<T extends View> extends FrameLayout implements OnLongClickListener {
    protected static final int IMAGE_LOADER_TIME_OUT = 15000;
    protected RelativeLayout mCoverageLayer = null;
    protected CircleProgress mProgress = null;
    protected TextView mDescTitle = null;
    protected TextView mDescContent = null;
    protected ImageView mSaveBtn = null;
    protected RelativeLayout mFailLayer = null;
    protected String mUrl = null;
    protected int mViewWidth = -1;
    protected int mViewHeight = -1;
    protected int mMaxWidth = -1;
    protected int mMaxHeight = -1;
    protected WeakReference<Context> mContextReference = null;

    public PhotoBase(Context context) {
        super(context);
        init(context, null);
    }

    public PhotoBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PhotoBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public PhotoBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        mContextReference = new WeakReference<Context>(context);
        setBackgroundColor(Color.BLACK);
        mInnerPhotoView = createPhotoView(context, attrs);
        addView(mInnerPhotoView, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutInflater.from(context).inflate(R.layout.layout_show_photo_view, this);
        mCoverageLayer = (RelativeLayout) findViewById(R.id.coverage_layer);
        mProgress = (CircleProgress) findViewById(R.id.circle_progress_view);
        mProgress.setParameter(Color.LTGRAY, Color.TRANSPARENT, Color.LTGRAY, Color.LTGRAY,
                BaseUtils.dp2px(context, 20), BaseUtils.dp2px(context, 2), -1, true);
        mDescTitle = (TextView) findViewById(R.id.img_title);
        mDescContent = (TextView) findViewById(R.id.img_describe);
        mSaveBtn = (ImageView) findViewById(R.id.save_imgview);
        mFailLayer = (RelativeLayout) findViewById(R.id.fail_layout);
        mSaveBtn.setOnClickListener(saveBtnListener);
        mCoverageLayer.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mFailLayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(STATUS_LOADING);
                loadImage(mUrl);
            }
        });
        mViewWidth = BaseUtils.getScreenWidth(context);// * 3 / 2;
        mViewHeight = BaseUtils.getScreenHeight(context);// * 3 / 2;
        mMaxWidth = BaseUtils.getScreenWidth(context) * 2;
        mMaxHeight = BaseUtils.getScreenHeight(context) * 2;
        setStatus(STATUS_LOADING);
    }

    protected T mInnerPhotoView = null;
    protected abstract T createPhotoView(Context context, AttributeSet attrs);

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    private OnClickListener saveBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!isSaving) {
                mSaveTask = createSaveTask(mUrl);
                mSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    };
    protected SaveAsyncTask createSaveTask(String url) {
        return new SaveAsyncTask(url);
    }

    protected static final int STATUS_LOADING = 1;
    protected static final int STATUS_FAIL = 2;
    protected static final int STATUS_SHOW = 3;
    private int mStatus = STATUS_LOADING;
    protected void setStatus(int status) {
        mStatus = status;
        onChangeStatus();
    }

    protected void onChangeStatus() {
        if (mStatus == STATUS_LOADING) {
            if (mInnerPhotoView != null) {
                mInnerPhotoView.setVisibility(INVISIBLE);
            }
            mCoverageLayer.setVisibility(VISIBLE);
            mProgress.setProgress(2);
            mFailLayer.setVisibility(GONE);
            mSaveBtn.setVisibility(GONE);
        } else if (mStatus == STATUS_SHOW) {
            if (mInnerPhotoView != null) {
                mInnerPhotoView.setVisibility(VISIBLE);
            }
            mCoverageLayer.setVisibility(GONE);
            mFailLayer.setVisibility(GONE);
            mSaveBtn.setVisibility(VISIBLE);
        } else if (mStatus == STATUS_FAIL) {
            if (mInnerPhotoView != null) {
                mInnerPhotoView.setVisibility(INVISIBLE);
            }
            mCoverageLayer.setVisibility(GONE);
            mFailLayer.setVisibility(VISIBLE);
            mSaveBtn.setVisibility(GONE);
        }
    }

    public void show(PhotoInfo info) {
        setStatus(STATUS_LOADING);
        if (info.isLocalPath) {
            showLocalImage(info.localPath);
        } else {
            mUrl = info.url;
            loadImage(info.url);
        }
        if (TextUtils.isEmpty(info.descTitle)) {
            mDescTitle.setVisibility(View.GONE);
        } else {
            mDescTitle.setVisibility(View.VISIBLE);
            mDescTitle.setText(info.descTitle);
        }
        if (TextUtils.isEmpty(info.descContent)) {
            mDescContent.setVisibility(View.GONE);
        } else {
            mDescContent.setVisibility(View.VISIBLE);
            mDescContent.setText(info.descContent);
        }
    }
    private void showLocalImage(String localPath) {
        setStatus(STATUS_SHOW);
        mSaveBtn.setVisibility(GONE);
        onDownloadImage(localPath);
    }
    public void destroy() {
        setStatus(STATUS_LOADING);
        mClickImageListener = null;
        isSaving = false;
        mDescTitle.setVisibility(View.GONE);
        mDescContent.setVisibility(View.GONE);
        if (mLoadTask != null) {
            mLoadTask.cancelTask();
            mLoadTask = null;
        }
        if (mSaveTask != null) {
            mSaveTask.cancelTask();
            mSaveTask = null;
        }
        destroyPhotoView();
    }
    protected abstract void destroyPhotoView();

    protected void loadImage(String url) {
        mLoadTask = new DownloadImageTask(url);
        mLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private DownloadImageTask mLoadTask = null;

    protected String mShowFilePath = null;
    protected abstract void onDownloadImage(String filePath);

    protected OnClickListener mClickImageListener = null;
    public void setClickImageListener(OnClickListener listener) {
        mClickImageListener = listener;
    }

    boolean isSaving = false;
    private SaveAsyncTask mSaveTask = null;
    class SaveAsyncTask extends AsyncTask<Void, Void, Boolean> {
        String url = null;
        String path = null;
        boolean isCanceled = false;

        public SaveAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSaving = true;
            Toast.makeText(mContextReference.get(), "图片正在保存...", Toast.LENGTH_SHORT).show();
            path = BaseUtils.getCachePathByUrl(url);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isCanceled) {
                return false;
            }
            path = download(path);
            if (path == null) {
                return false;
            }
            try {
                MediaStore.Images.Media.insertImage(
                        mContextReference.get().getContentResolver(),
                        path, System.currentTimeMillis() + "", "");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            onEnd(result);
        }
        public void cancelTask() {
            isCanceled = true;
            doOnCancel();
            cancel(true);
        }

        protected void onEnd(boolean success) {
            isSaving = false;
            if (isCanceled) {
                return ;
            }
            if (success) {
                Toast.makeText(mContextReference.get(), "图片已保存到相册了哦~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContextReference.get(), "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        }
        public void start() {
        }
        protected String download(String filePath) {
            if (BaseUtils.isFileExistAndNotNull(filePath)) {
                Options opts = new Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opts);
                if (opts.outHeight > 0 && opts.outWidth > 0) {
                    return filePath;
                } else {
                    new File(filePath).delete();
                }
            }
            BufferedInputStream is = null;
            int fileSize = -1;
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setConnectTimeout(IMAGE_LOADER_TIME_OUT);
                conn.setReadTimeout(IMAGE_LOADER_TIME_OUT);
                conn.setInstanceFollowRedirects(true);
                fileSize = conn.getContentLength();
                is = new BufferedInputStream(conn.getInputStream(), BaseUtils.BUFFER_SIZE);
            } catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
            boolean isLoadSuccess = true;
            File f = null;
            try {
                f = new File(filePath);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(f), BaseUtils.BUFFER_SIZE);
                try {
                    float curSize = 0f;
                    byte[] bytes=new byte[BUFFER_SIZE];
                    while (true) {
                        if (isCanceled) {
                            isLoadSuccess = false;
                            break;
                        }
                        int count = is.read(bytes, 0, BUFFER_SIZE);
                        if (count == -1) {
                            break;
                        }
                        os.write(bytes, 0, count);
                        curSize += count;
                    }
                } finally {
                    try {
                        os.close();
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            } catch (Exception ex){
                ex.printStackTrace();
                if (f != null) {
                    f.delete();
                }
                isLoadSuccess = false;
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    // Do nothing
                }
            }
            if (isLoadSuccess) {
                return filePath;
            }
            return null;
        }
        protected void doOnCancel() {

        }
    }

    /**
     * http下载中的InputStream改为BufferedInputStream,增加缓冲区大小为  32 KB
     */
    static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    class DownloadImageTask extends AsyncTask<String, Float, Boolean> {
        private String url = null;
        private String filePath = null;
        private boolean isCanceled = false;
        public DownloadImageTask(String url) {
            this.url = url;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            if (TextUtils.isEmpty(url)) {
                return false;
            }
            filePath = BaseUtils.getCachePathByUrl(url);
            if (BaseUtils.isFileExistAndNotNull(filePath)) {
                Options opts = new Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opts);
                if (opts.outHeight > 0 && opts.outWidth > 0) {
                    return true;
                } else {
                    new File(filePath).delete();
                }
            }
            BufferedInputStream is = null;
            int fileSize = -1;
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setConnectTimeout(IMAGE_LOADER_TIME_OUT);
                conn.setReadTimeout(IMAGE_LOADER_TIME_OUT);
                conn.setInstanceFollowRedirects(true);
                fileSize = conn.getContentLength();
                is = new BufferedInputStream(conn.getInputStream(), BaseUtils.BUFFER_SIZE);
            } catch (Exception ex){
                ex.printStackTrace();
                return false;
            }
            boolean isLoadSuccess = true;
            File f = null;
            try {
                f = new File(filePath);
                if (!f.exists()) {
                    f.createNewFile();
                }
                OutputStream os = new BufferedOutputStream(new FileOutputStream(f), BaseUtils.BUFFER_SIZE);
                try {
                    float curSize = 0f;
                    byte[] bytes=new byte[BUFFER_SIZE];
                    while (true) {
                        int count = is.read(bytes, 0, BUFFER_SIZE);
                        if (count == -1) {
                            break;
                        }
                        os.write(bytes, 0, count);
                        curSize += count;
                        if (fileSize > 0) {
                            publishProgress(curSize / fileSize);
                        }
                    }
                } finally {
                    try {
                        os.close();
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            } catch (Exception ex){
                ex.printStackTrace();
                if (f != null) {
                    f.delete();
                }
                isLoadSuccess = false;
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    // Do nothing
                }
            }
            return isLoadSuccess;
        }

        @Override
        protected void onProgressUpdate(Float... voids) {
            mProgress.setProgress((int)(voids[0] * 100));
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (isCanceled) {
                Log.i("", "canceled, do nothing");
                return ;
            }
            if (aBoolean) {
                setStatus(STATUS_SHOW);
                mShowFilePath = filePath;
                onDownloadImage(filePath);
            } else {
                setStatus(STATUS_FAIL);
            }
        }
        public void cancelTask() {
            isCanceled = true;
            cancel(true);
        }
    }

}
