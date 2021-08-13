package com.emreduver.messageapplication.services.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    companion object{

        fun <T> buildService(baseUrl: String, retrofitService: Class<T>, existInterceptor: Boolean) : T{

            val clientBuilder = OkHttpClient.Builder()
            //clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            //clientBuilder.addInterceptor(NetworkInterceptor());

            if (existInterceptor){
                //clientBuilder.addInterceptor(TokenInterceptor())
            }
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create()
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(retrofitService)
        }
    }
}