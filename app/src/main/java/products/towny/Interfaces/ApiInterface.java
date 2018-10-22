package products.towny.Interfaces;

import products.towny.Models.OTPResponse;
import products.towny.Models.Offers;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("login/getotp")
    Call<OTPResponse> GetOTP(@Query("phone_no") String phoneNo);

    @GET("Retrive/getoffers")
    Call<Offers> GetOffers(@Query("lat") String latitude, @Query("long") String longitude, @Query("rad") String radius);
}