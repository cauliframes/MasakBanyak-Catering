package io.cauliframes.masakbanyak_catering.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import io.cauliframes.masakbanyak_catering.di.SessionScope;
import io.cauliframes.masakbanyak_catering.model.Catering;
import io.cauliframes.masakbanyak_catering.model.Packet;
import io.cauliframes.masakbanyak_catering.ui.activity.LoginActivity;
import io.cauliframes.masakbanyak_catering.util.Util;
import io.cauliframes.masakbanyak_catering.webservice.MasakBanyakWebService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SessionScope
public class CateringRepository {
  private MutableLiveData<Catering> cateringLiveData = new MutableLiveData<>();
  private MutableLiveData<ArrayList<Packet>> packetsLiveData = new MutableLiveData<>();
  private MutableLiveData<Packet> packetLiveData = new MutableLiveData<>();
  private MutableLiveData<Util.Event<String>> notificationEventLiveData = new MutableLiveData<>();
  
  private Context context;
  private SharedPreferences preferences;
  private JWT jwt;
  private MasakBanyakWebService webService;
  
  @Inject
  public CateringRepository(Context context, SharedPreferences preferences, JWT jwt, MasakBanyakWebService webService) {
    this.context = context;
    this.preferences = preferences;
    this.jwt = jwt;
    this.webService = webService;
    
    refreshCatering();
    refreshPackets();
  }
  
  public LiveData<Catering> getCateringLiveData() {
    return cateringLiveData;
  }
  
  public LiveData<ArrayList<Packet>> getPacketsLiveData() {
    return packetsLiveData;
  }
  
  public LiveData<Packet> getPacketLiveData(String packet_id) {
    refreshPacket(packet_id);
    return packetLiveData;
  }
  
  public LiveData<Util.Event<String>> getNotificationEventLiveData() {
    return notificationEventLiveData;
  }
  
  public void refreshCatering() {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
      String catering_id = jwt.getClaim("catering_id").asString();
      
      Call<Catering> call = webservice.getCatering(authorization, catering_id);
      
      call.enqueue(new Callback<Catering>() {
        @Override
        public void onResponse(Call<Catering> call, Response<Catering> response) {
          if (response.isSuccessful()) {
            cateringLiveData.postValue(response.body());
          }
        }
        
        @Override
        public void onFailure(Call<Catering> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void uploadCateringAvatar(Catering catering, String filename, byte[] file) {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
      
      RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
      MultipartBody.Part formData = MultipartBody.Part.createFormData("avatar", filename, requestBody);
      
      Call<ResponseBody> call = webservice.uploadAvatar(authorization, catering.getCatering_id(), formData);
      
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.isSuccessful()) {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.body().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          refreshCatering();
        }
        
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void updateCatering(Catering catering) {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
      String catering_id = jwt.getClaim("catering_id").asString();
      
      Call<ResponseBody> call = webservice.updateProfile(authorization, catering_id,
          catering.getName(), catering.getAddress(), catering.getPhone());
      
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.isSuccessful()) {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.body().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.errorBody().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void refreshPackets() {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (String access_token, MasakBanyakWebService service) -> {
      String authorization = "Bearer " + access_token;
      String catering_id = jwt.getClaim("catering_id").asString();
      
      Call<ArrayList<Packet>> call = service.getPacketsByCatering(authorization, catering_id);
      
      call.enqueue(new Callback<ArrayList<Packet>>() {
        @Override
        public void onResponse(Call<ArrayList<Packet>> call, Response<ArrayList<Packet>> response) {
          if (response.isSuccessful()) {
            packetsLiveData.postValue(response.body());
          }
        }
        
        @Override
        public void onFailure(Call<ArrayList<Packet>> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void refreshPacket(String packet_id) {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
      
      Call<Packet> call = webservice.getPacketById(authorization, packet_id);
      
      call.enqueue(new Callback<Packet>() {
        @Override
        public void onResponse(Call<Packet> call, Response<Packet> response) {
          if (response.isSuccessful()) {
            packetLiveData.postValue(response.body());
          }
        }
        
        @Override
        public void onFailure(Call<Packet> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void uploadPacketImage(Packet packet, String filename, byte[] file, int code){
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
  
      RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
      MultipartBody.Part formData = MultipartBody.Part.createFormData("image", filename, requestBody);
      
      Call<ResponseBody> call = webservice.uploadPacketImage(authorization, packet.getPacket_id(), formData, code);
  
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.isSuccessful()) {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.body().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          refreshPacket(packet.getPacket_id());
        }
    
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void addPacket(Packet packet) {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
      String catering_id = jwt.getClaim("catering_id").asString();
      
      Call<ResponseBody> call = webservice.addPacket(authorization, catering_id, packet);
      
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.isSuccessful()) {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.body().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.errorBody().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void updatePacket(Packet packet) {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer " + access_token;
      
      Call<ResponseBody> call = webservice.updatePacket(authorization, packet.getPacket_id(), packet);
      
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.isSuccessful()) {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.body().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            try {
              notificationEventLiveData.postValue(new Util.Event<>(response.errorBody().string()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
  
  public void deletePacket(Packet packet) {
  
  }
  
  public void logout(Catering catering) {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String refresh_token = preferences.getString("refresh_token", null);
      String catering_id = catering.getCatering_id();
      
      Call<ResponseBody> call = webservice.logout(refresh_token, catering_id);
      
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.isSuccessful()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
          }
        }
        
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
}