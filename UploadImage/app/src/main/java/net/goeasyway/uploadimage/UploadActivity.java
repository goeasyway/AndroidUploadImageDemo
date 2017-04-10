package net.goeasyway.uploadimage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.goeasyway.uploadimage.model.UploadResult;
import net.goeasyway.uploadimage.retrofit.ApiService;
import net.goeasyway.uploadimage.retrofit.PhotoApiService;
import net.goeasyway.uploadimage.retrofit.PhotoRequestBody;
import net.goeasyway.uploadimage.retrofit.UploadCallback;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private static final int SELECT_PIC = 1;

    private TextView textView;

    private String imagePath;
    private PhotoApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        apiService = ApiService.getInstance();

        Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFromGallery();
            }
        });

        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(imagePath);
            }
        });

        textView = (TextView) findViewById(R.id.photoPath);
    }

    private void selectFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SELECT_PIC:
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri,  projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    imagePath = cursor.getString(0);
                    textView.setText(imagePath);
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(String path) {
        File file = new File(path);
        if (file == null || !file.exists()) {
            return;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        PhotoRequestBody photoRequestBody = new PhotoRequestBody(body, new UploadProgress());
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), photoRequestBody);

        Call<UploadResult> call = apiService.uploadPhoto(part);
        call.enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                Log.i("Upload", "上传成功！");
                UploadResult result = response.body();
                if (result.getCode() != 0) {
                    textView.setText("上传成功！url: " + result.getUrl());
                } else {
                    textView.setText("上传失败！error: " + result.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {
                Log.i("Upload", "上传失败！error: " + t.toString());
                textView.setText("上传失败！");
            }
        });
    }

    private class UploadProgress implements UploadCallback {

        @Override
        public void onProgress(final long progress, final long total) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("上传进度：" + progress * 100/total + "%");
                }
            });

            Log.i("Upload", "上传进度：" + progress * 100/total + "%");
        }
    }
}
