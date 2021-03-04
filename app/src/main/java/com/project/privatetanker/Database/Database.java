package com.project.privatetanker.Database;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Database {private static final String BASE_URL="http://androginex.com/privatetanker/public/api/";
    private static Retrofit retrofit=null;

    public static Retrofit getClient(Context context){

        if (retrofit==null){
            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original =chain.request();

                            Request.Builder requestBuilder=original.newBuilder()
                                    .method(original.method(),original.body());
                            Request request=requestBuilder.build();
                            return chain.proceed(request);
                        }
                    }).build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }


}
