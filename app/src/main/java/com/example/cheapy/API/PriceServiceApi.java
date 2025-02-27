package com.example.cheapy.API;

import com.example.cheapy.entities.ItemStoreRequest;
import com.example.cheapy.entities.Price;
import com.example.cheapy.entities.StoreTotalRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PriceServiceApi {
    @POST("Price/getItemPriceByStore")
    Call<Double> getItemPriceByStore(
            @Header("Authorization") String token,
            @Body ItemStoreRequest request
    );
    @POST("Price/getTotalPriceByStore")
    Call<Double> getTotalPriceByStore(
            @Header("Authorization") String token,
            @Body StoreTotalRequest request
    );
}
