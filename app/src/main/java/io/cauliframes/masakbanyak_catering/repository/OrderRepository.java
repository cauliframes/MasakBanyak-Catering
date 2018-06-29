package io.cauliframes.masakbanyak_catering.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.ArrayList;

import javax.inject.Inject;

import io.cauliframes.masakbanyak_catering.di.SessionScope;
import io.cauliframes.masakbanyak_catering.model.Order;
import io.cauliframes.masakbanyak_catering.util.Util;
import io.cauliframes.masakbanyak_catering.webservice.MasakBanyakWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SessionScope
public class OrderRepository {
  private MutableLiveData<ArrayList<Order>> ordersLiveData = new MutableLiveData<>();
  
  private SharedPreferences preferences;
  private JWT jwt;
  private MasakBanyakWebService webService;
  
  @Inject
  public OrderRepository(SharedPreferences preferences, JWT jwt, MasakBanyakWebService webService) {
    this.preferences = preferences;
    this.jwt = jwt;
    this.webService = webService;
    
    refreshOrders();
  }
  
  public LiveData<ArrayList<Order>> getOrdersLiveData(){
    return ordersLiveData;
  }
  
  public void refreshOrders() {
    Util.authorizeAndExecuteCall(preferences, jwt, webService, (access_token, webservice) -> {
      String authorization = "Bearer "+access_token;
      String catering_id = jwt.getClaim("catering_id").asString();
  
      Call<ArrayList<Order>> call = webservice.getOrdersByCustomer(authorization, catering_id);
      
      call.enqueue(new Callback<ArrayList<Order>>() {
        @Override
        public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
          if(response.isSuccessful()){
            ordersLiveData.postValue(response.body());
          }
        }
  
        @Override
        public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
          Log.d("Network Call Failure", t.toString());
        }
      });
    });
  }
}
