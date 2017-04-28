package com.icechn.library.showphoto.Utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * Created by ICE on 2017/3/4.
 */

public class FrescoHelper {
    private static final String IMAGE_PIPELINE_CACHE_DIR = "ImagepipelineDiskCache";
    private static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;
    private static final int MAX_LENGTH_OF_EVICTION_QUEUE = 20;

    public static void initFresco(Context context, String bufferPath,
                                  MemoryTrimmableRegistry memoryTrimmableRegistry) {
        ImagePipelineConfig sImagePipelineConfig;
        OkHttpClient okHttpClient = new OkHttpClient();
        ImagePipelineConfig.Builder configBuilder =
                ImagePipelineConfig.newBuilder(context)
                        .setNetworkFetcher(new OkHttpNetworkFetcher(okHttpClient));
        configureCaches(configBuilder, context, bufferPath, memoryTrimmableRegistry);
        configBuilder.setDownsampleEnabled(true);
        sImagePipelineConfig = configBuilder.build();
        Fresco.initialize(context, sImagePipelineConfig);
    }
    public static void shutDown() {
        Fresco.shutDown();
    }

    private static void configureCaches(
            ImagePipelineConfig.Builder configBuilder,
            Context context, final String bufferPath,
            MemoryTrimmableRegistry memoryTrimmableRegistry) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,       // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE / 2, // Max total size of elements in eviction queue
                MAX_LENGTH_OF_EVICTION_QUEUE, // Max length of eviction queue
                Integer.MAX_VALUE);      // Max cache entry size
        configBuilder
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder(context)
                                .setBaseDirectoryPath(getCacheDir(context, bufferPath))
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                                .build())
                .setMemoryTrimmableRegistry(memoryTrimmableRegistry);
    }

    private static File getCacheDir(Context context, String bufferPath) {
        if(BaseUtils.getSDCardDir() != null && !TextUtils.isEmpty(bufferPath)){
            BaseUtils.mkdirs(bufferPath);
            return new File(bufferPath);
        } else {
            return context.getApplicationContext().getCacheDir();
        }
    }

    public static Uri getLocalFileUri(String filePath) {
        return UriUtil.getUriForFile(new File(filePath));
    }


    /**
     * 删除内存缓存
     * @param uri
     */
    public static void removeCacheFromMem(Uri uri) {
        if (uri == null) {
            return ;
        }
        Fresco.getImagePipeline().evictFromMemoryCache(uri);
    }


    /**
     * 是否在内存缓存中
     * @param uri
     * @return
     */
    public static boolean isInMemCache(Uri uri) {
        if (uri == null) {
            return false;
        }
        return Fresco.getImagePipeline().isInBitmapMemoryCache(uri);
    }

}
