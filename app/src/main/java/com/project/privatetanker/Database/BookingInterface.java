package com.project.privatetanker.Database;

import com.project.privatetanker.Models.Bookings.BookingListResponse;
import com.project.privatetanker.Models.Bookings.BookingModel;
import com.project.privatetanker.Models.Bookings.BookingResponse;
import com.project.privatetanker.Models.Capacity.CapacityListResponse;
import com.project.privatetanker.Models.Charges.ChargeListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingInterface {

    @GET("getTankerCapacity/{areaId}")
    Call<CapacityListResponse> getTankerCapacity(@Path("areaId")Integer areaId);

    @POST("bookTanker")
    Call<BookingResponse>bookTanker(@Body BookingModel bookingModel);

    @GET("getBookingDetails/{bookingId}")
    Call<BookingResponse>getBookingDetails(@Path("bookingId")Integer bookingId);

    @GET("cancelBooking/{bookingId}")
    Call<BookingResponse>cancelBooking(@Path("bookingId")Integer bookingId);

    @POST("modifyBooking")
    Call<BookingResponse>modifyBooking(@Body BookingModel bookingModel);

    @GET("getBookingList/{userId}")
    Call<BookingListResponse>getBookingList(@Path("userId")Integer userId);

    @GET("getChargesByCID/{capacityId}")
    Call<ChargeListResponse>getChargesByCapacityId(@Path("capacityId")Integer capacityId);

}
