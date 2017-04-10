package net.goeasyway.uploadimage.retrofit;

/**
 * Created by lan on 17/4/8.
 */

public interface UploadCallback {

    /**
     * 上传进度
     * @param progress 当前已上传的大小
     * @param total 文件总大小
     */
    void onProgress(long progress, long total);
}
