package com.example.sharan.iotsmartchain.di.module;

import android.app.Application;

import com.example.sharan.iotsmartchain.App;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Sharan on 20-03-2018.
 */
@Module
public class ApiServiceModule {

    private final String END_POINT_URL;

    public ApiServiceModule(String endPointUrl) {
        END_POINT_URL = endPointUrl;
    }

    /*@Provides
    @Singleton
    IApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(IApiService.class);
    }*/

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Named("cached")
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache, Interceptor interceptor) {
        OkHttpClient client = new OkHttpClient();
        try{
            client.interceptors().add(interceptor);
        }catch (Exception e){
            e.printStackTrace();
        }
        client.setCache(cache);
        return client;
    }

    @Provides
    @Named("non_cached")
    @Singleton
    OkHttpClient provideOkHttpClient_non_cached(Interceptor interceptor) {
        OkHttpClient client = new OkHttpClient();
        try{
            client.interceptors().add(interceptor);
        } catch(RuntimeException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return client;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, @Named("cached") OkHttpClient okHttpClient) {
        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        return new Retrofit.Builder()
                .baseUrl(END_POINT_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Named("ApiServiceUrl")
    String provideApiServiceUrl() {
        return END_POINT_URL;
    }

    @Provides
    Interceptor provideInterceptor(@Named("AuthToken") String authToken, @Named("LoginId") String loginId) {

        return (Interceptor.Chain chain) -> {
            Request request = chain.request();

            if (!authToken.isEmpty() && !loginId.isEmpty()) {
                Request.Builder reqBuilder = request.newBuilder()
                        .header("email-id", loginId)
                        .header("x-access-token", authToken)
                        .method(request.method(), request.body());

                request = reqBuilder.build();
            }
            try{
                return chain.proceed(request); //Exception java.net.SocketTimeoutException
            }
            catch (SocketTimeoutException e){
                return null;
            } catch (ConnectException connectException){
                return null;
            }

        };
    }

    @Provides
    @Named("AuthToken")
    String provideAuthToken() {
        return App.getSharedPrefsComponent()
                .getSharedPrefs()
                .getString("TOKEN", "");
    }

    @Provides
    @Named("LoginId")
    String provideLoginId() {
        return App.getSharedPrefsComponent()
                .getSharedPrefs()
                .getString("AUTH_EMAIL_ID", "");
    }
}
