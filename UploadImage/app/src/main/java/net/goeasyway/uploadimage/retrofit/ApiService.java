package net.goeasyway.uploadimage.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lan on 17/4/9.
 */

public class ApiService {

    private static PhotoApiService apiService;

    public static PhotoApiService getInstance() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.4:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(PhotoApiService.class);
        }

        return apiService;
    }
}
