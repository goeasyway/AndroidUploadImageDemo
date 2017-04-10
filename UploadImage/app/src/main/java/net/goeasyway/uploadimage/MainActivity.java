package net.goeasyway.uploadimage;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.goeasyway.uploadimage.model.Photo;
import net.goeasyway.uploadimage.retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.actionBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPhotos();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPhotos();
    }

    private void loadPhotos() {
        Call<List<Photo>> call = ApiService.getInstance().getAllPhotos();
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {

                swipeRefreshLayout.setRefreshing(false);

                List<Photo> photos = response.body();
                if (photos != null) {
                    recyclerView.setAdapter(new PhotoAdapter(photos));
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

        private List<Photo> photos;

        public PhotoAdapter(List<Photo> photos) {
            this.photos = photos;
            if (this.photos == null) {
                this.photos = new ArrayList<Photo>();
            }
        }


        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            Photo photo = photos.get(position);
            Glide.with(MainActivity.this).load(photo.getUrl()).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }


    }
}
