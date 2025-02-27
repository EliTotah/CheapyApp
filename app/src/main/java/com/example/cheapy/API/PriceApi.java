package com.example.cheapy.API;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.cheapy.Cheapy;
import com.example.cheapy.entities.Item;
import com.example.cheapy.entities.ItemStoreRequest;
import com.example.cheapy.entities.Price;
import com.example.cheapy.entities.StoreTotalRequest;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PriceApi {
    private final PriceServiceApi priceServiceApi;

    public PriceApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        String apiAddress = Cheapy.urlServer.getValue();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiAddress + "/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        priceServiceApi = retrofit.create(PriceServiceApi.class);
    }

    public void getItemPriceByStore(String token, String itemId, String storeId, MutableLiveData<Double> priceLiveData) {
        ItemStoreRequest request = new ItemStoreRequest(storeId, itemId);
        Call<Double> call = priceServiceApi.getItemPriceByStore(token, request);
        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    priceLiveData.setValue(response.body());
                } else {
                    Toast.makeText(Cheapy.context, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    priceLiveData.setValue(0.0);
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                Toast.makeText(Cheapy.context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                priceLiveData.setValue(0.0);
            }
        });
    }

    public void getTotalPriceByStore(String token, String storeName, List<Item> items, MutableLiveData<Double> totalPriceLiveData) {
        StoreTotalRequest request = new StoreTotalRequest(storeName, items);
        Call<Double> call = priceServiceApi.getTotalPriceByStore(token, request);
        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful()) {
                    totalPriceLiveData.setValue(response.body());
                } else {
                    totalPriceLiveData.setValue(0.0);
                }
            }
            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                totalPriceLiveData.setValue(0.0);
            }
        });
    }

}
