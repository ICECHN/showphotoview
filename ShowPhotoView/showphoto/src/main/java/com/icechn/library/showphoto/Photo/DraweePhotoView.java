package com.icechn.library.showphoto.Photo;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.icechn.library.showphoto.Progress.PhotoLoadingProgress;
import com.icechn.library.showphoto.Utils.FrescoHelper;
import com.icechn.library.showphoto.ZoomDrawee.OnViewTapListener;
import com.icechn.library.showphoto.ZoomDrawee.ZoomDraweeView;


/**
 * Created by ICE on 2016/12/23.
 */

public class DraweePhotoView extends PhotoBase<ZoomDraweeView> {

    public DraweePhotoView(Context context) {
        super(context);
    }

    public DraweePhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraweePhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DraweePhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected ZoomDraweeView createPhotoView(Context context, AttributeSet attrs) {
        return new InternalZoomDraweeView(context, attrs);
    }

    protected boolean showPhotoOnline() {
        return true;
    }

    @Override
    protected void onDownloadImage(String filePath) {
        setImageUri(FrescoHelper.getLocalFileUri(filePath), false);
    }

    @Override
    protected SaveAsyncTask createSaveTask(String url) {
        if (showPhotoOnline()) {
            return new SaveAsyncTask(url);
        } else {
            return super.createSaveTask(url);
        }
    }

    @Override
    protected void loadImage(String url) {
        if (TextUtils.isEmpty(url)) {
            setStatus(STATUS_FAIL);
            return ;
        }
        if (showPhotoOnline()) {
            setStatus(STATUS_SHOW);
            setImageUri(Uri.parse(url), true);
        } else {
            super.loadImage(url);
        }
    }

    private void setImageUri(Uri uri, boolean needProgress) {
        if (needProgress) {
            if (!FrescoHelper.isInMemCache(uri)) {
                mInnerPhotoView.getHierarchy().setProgressBarImage(new PhotoLoadingProgress(mContextReference.get()));
            }
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(mViewWidth, mViewHeight))
                .setRotationOptions(RotationOptions.autoRotate())
                .build();
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mInnerPhotoView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null) {
                            return;
                        }
                        int tmpWidth = imageInfo.getWidth();
                        int tmpHeight = imageInfo.getHeight();
                        while(tmpWidth > mMaxWidth || tmpHeight > mMaxHeight) {
                            tmpWidth /= 2;
                            tmpHeight /= 2;
                        }
                        mInnerPhotoView.update(tmpWidth, tmpHeight);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        setStatus(STATUS_FAIL);
                    }
                });
        mInnerPhotoView.setController(controller.build());
    }

    @Override
    protected void destroyPhotoView() {
        mInnerPhotoView.setOnViewTapListener(null);
        mInnerPhotoView.setImageURI((Uri)null);
        mInnerPhotoView.setVisibility(GONE);
        if (!TextUtils.isEmpty(mShowFilePath)) {
            FrescoHelper.removeCacheFromMem(FrescoHelper.getLocalFileUri(mShowFilePath));
            mShowFilePath = null;
        }
        if (!TextUtils.isEmpty(mUrl)) {
            mUrl = null;
        }
    }
    class InternalZoomDraweeView extends ZoomDraweeView {

        public InternalZoomDraweeView(Context context) {
            super(context);
            initTapListener();
        }

        public InternalZoomDraweeView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initTapListener();
        }

        public InternalZoomDraweeView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initTapListener();
        }
        private void initTapListener() {
            setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (mClickImageListener != null) {
                        mClickImageListener.onClick(view);
                    }
                }
            });
            setOnLongClickListener(DraweePhotoView.this);
        }
    }

}
