package com.project.privatetanker.Database;

import com.project.privatetanker.Models.Address.AddAddressModel;
import com.project.privatetanker.Models.Address.AddAddressResponse;
import com.project.privatetanker.Models.Address.AddressListResponse;
import com.project.privatetanker.Models.Address.AddressResponse;
import com.project.privatetanker.Models.Address.CheckLocationModel;
import com.project.privatetanker.Models.Address.CheckLocationResponse;
import com.project.privatetanker.Models.Connections.AddConnectionModel;
import com.project.privatetanker.Models.Connections.AddConnectionResponse;
import com.project.privatetanker.Models.Connections.ConnectionResponse;
import com.project.privatetanker.Models.Connections.RemoveConnectionModel;
import com.project.privatetanker.Models.Login.LoginModel;
import com.project.privatetanker.Models.Login.LoginResponse;
import com.project.privatetanker.Models.UpdateTokenModel;
import com.project.privatetanker.Models.User.ChangeMobileModel;
import com.project.privatetanker.Models.User.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserInterface {

    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginModel loginModel);

    @GET("user/{userId}")
    Call<UserResponse>user(@Path("userId")Integer userId);

    @GET("addresses/{userId}")
    Call<AddressListResponse>addresses(@Path("userId")Integer userId);

    @GET("connections/{userId}")
    Call<ConnectionResponse>connections(@Path("userId")Integer userId);

    @POST("addUser")
    Call<AddConnectionResponse>addConnection(@Body AddConnectionModel addConnectionModel);

    @POST("removeUser}")
    Call<ConnectionResponse>removeConnection(@Body RemoveConnectionModel removeConnectionModel);

    @Multipart
    @POST("editProfile")
    Call<UserResponse>updateProfile(
            @Part("user_id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("customer_type") RequestBody customer_type,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("editProfile")
    Call<UserResponse>updateProfile(
            @Part("user_id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("customer_type") RequestBody customer_type
    );

    @Multipart
    @POST("completeProfile")
    Call<UserResponse>completeProfile(
            @Part("user_id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("customer_type") RequestBody customer_type,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part image
    );

    @POST("changeMobile")
    Call<UserResponse>changeMobile(@Body ChangeMobileModel changeMobileModel);

    @POST("addAddress")
    Call<AddAddressResponse>addAddress(@Body AddAddressModel addAddressModel);

    @POST("editAddress")
    Call<AddAddressResponse>editAddress(@Body AddAddressModel addAddressModel);

    @GET("deleteAddress/{addressId}")
    Call<AddAddressResponse>deleteAddress(@Path("addressId")Integer addressId);

    @GET("getAddressById/{addressId}")
    Call<AddressResponse>getAddressById(@Path("addressId")Integer addressId);

    @POST("checkServiceable")
    Call<CheckLocationResponse>checkServiceable(@Body CheckLocationModel checkLocationModel);

    @POST("updateToken")
    Call<UserResponse>updateToken(@Body UpdateTokenModel updateTokenModel);
}
