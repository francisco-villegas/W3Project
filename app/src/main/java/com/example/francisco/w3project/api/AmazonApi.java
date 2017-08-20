package com.example.francisco.w3project.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AmazonApi {

    private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://de-coding-test.s3.amazonaws.com";

    public static Retrofit getClient() {
        if (retrofit == null) {
            //created a logging interceptor
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            //logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
