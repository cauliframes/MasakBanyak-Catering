package io.cauliframes.masakbanyak_catering.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.cauliframes.masakbanyak_catering.model.Order;
import io.cauliframes.masakbanyak_catering.repository.OrderRepository;
import io.cauliframes.masakbanyak_catering.util.Util;

public class OrderViewModel extends ViewModel {
  private OrderRepository repository;
  
  private LiveData<ArrayList<Order>> ordersLiveData;
  private LiveData<Util.Event<String>> notificationEventLiveData;
  
  @Inject
  public OrderViewModel(OrderRepository repository) {
    this.repository = repository;
    this.ordersLiveData = repository.getOrdersLiveData();
    this.notificationEventLiveData = repository.getNotificationEventLiveData();
  }
  
  public LiveData<ArrayList<Order>> getOrdersLiveData() {
    return ordersLiveData;
  }
  
  public LiveData<Util.Event<String>> getNotificationEventLiveData() {
    return notificationEventLiveData;
  }
  
  public void refreshOrders() {
    repository.refreshOrders();
  }
  
  public void cancelOrder(Order order) {
    repository.cancelOrder(order);
  }
}