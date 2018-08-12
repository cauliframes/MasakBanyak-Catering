package io.cauliframes.masakbanyak_catering.webservice;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.cauliframes.masakbanyak_catering.model.Catering;
import io.cauliframes.masakbanyak_catering.model.Customer;
import io.cauliframes.masakbanyak_catering.model.Order;
import io.cauliframes.masakbanyak_catering.model.Packet;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MasakBanyakWebService {
  @FormUrlEncoded
  @POST("/auth/catering/register")
  Call<ResponseBody> register(
      @Field("name") String name,
      @Field("address") String address,
      @Field("phone") String phone,
      @Field("email") String email,
      @Field("password") String password
  );
  
  @FormUrlEncoded
  @POST("/auth/catering/login")
  Call<JsonObject> login(
      @Field("email") String email,
      @Field("password") String password
  );
  
  @FormUrlEncoded
  @POST("/auth/catering/refresh")
  Call<JsonObject> refresh(
      @Field("refresh_token") String refresh_token,
      @Field("catering_id") String catering_id
  );
  
  @FormUrlEncoded
  @POST("/auth/catering/logout")
  Call<ResponseBody> logout(
      @Field("refresh_token") String refresh_token,
      @Field("catering_id") String catering_id
  );
  
  @GET("/caterings/{id}")
  Call<Catering> getCatering(
      @Header("Authorization") String authorization,
      @Path("id") String catering_id
  );
  
  @FormUrlEncoded
  @PUT("/caterings/{id}/update")
  Call<ResponseBody> updateProfile(
      @Header("Authorization") String authorization,
      @Path("id") String catering_id,
      @Field("name") String name,
      @Field("address") String address,
      @Field("phone") String phone
  );
  
  @Multipart
  @POST("/caterings/{id}/avatar")
  Call<ResponseBody> uploadAvatar(
      @Header("Authorization") String authorization,
      @Path("id") String catering_id,
      @Part() MultipartBody.Part file
  );
  
  @GET("/caterings/{id}/packets")
  Call<ArrayList<Packet>> getPacketsByCatering(@Header("Authorization") String authorization, @Path("id") String catering_id);
  
  @POST("/caterings/{id}/packets")
  Call<ResponseBody> addPacket(@Header("Authorization") String authorization, @Path("id") String catering_id, @Body Packet packet);
  
  @GET("/caterings/{id}/orders")
  Call<ArrayList<Order>> getOrdersByCustomer(@Header("Authorization") String authorization, @Path("id") String catering_id);
  
  @GET("/packets/{id}")
  Call<Packet> getPacketById(@Header("Authorization") String authorization, @Path("id") String packet_id);
  
  @Multipart
  @POST("/packets/{id}/image")
  Call<ResponseBody> uploadPacketImage(
      @Header("Authorization") String authorization,
      @Path("id") String packet_id,
      @Part() MultipartBody.Part file,
      @Part("code") int code
  );
  
  @PUT("/packets/{id}")
  Call<ResponseBody> updatePacket(@Header("Authorization") String authorization, @Path("id") String packet_id, @Body Packet packet);
  
  @GET("/customers/{id}")
  Call<Customer> getCustomer(@Header("Authorization") String authorization, @Path("id") String customer_id);
  
  @POST("/orders/{id}/cancel")
  Call<ResponseBody> cancelOrder(@Header("Authorization") String authorization, @Path("id") String order_id);
}