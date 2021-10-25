package com.example.football_field_booking.utils;

import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APISERVICE {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAOw-vlsM:APA91bE97y9Ql_yIivLBTgWJp0esMZ3VVVeasd2H1YuT83kIvN3Pm1xkkHym0M0_AMgjal04moeO8jGmPZTrWUqm_QtJOZU0eBZrUBlpWfvwZVYyVSaTup8OUaRmYSiwh67LT3Hlh5mL"
    })

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body Sender body);
}
