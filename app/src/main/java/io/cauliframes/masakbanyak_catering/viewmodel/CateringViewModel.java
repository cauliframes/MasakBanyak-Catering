package io.cauliframes.masakbanyak_catering.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import java.util.ArrayList;

import javax.inject.Inject;

import io.cauliframes.masakbanyak_catering.model.Catering;
import io.cauliframes.masakbanyak_catering.model.Packet;
import io.cauliframes.masakbanyak_catering.repository.CateringRepository;
import io.cauliframes.masakbanyak_catering.util.Util;

public class CateringViewModel extends ViewModel {
  private LiveData<Catering> cateringLiveData;
  private LiveData<ArrayList<Packet>> packetsLiveData;
  private LiveData<Packet> packetLiveData;
  private LiveData<Util.Event<String>> notificationLiveData;
  
  private CateringRepository repository;
  
  @Inject
  public CateringViewModel(CateringRepository repository) {
    this.repository = repository;
    this.cateringLiveData = repository.getCateringLiveData();
    this.packetsLiveData = repository.getPacketsLiveData();
    this.notificationLiveData = repository.getNotificationEventLiveData();
  }
  
  public LiveData<Catering> getCateringLiveData() {
    return cateringLiveData;
  }
  
  public LiveData<ArrayList<Packet>> getPacketsLiveData() {
    return packetsLiveData;
  }
  
  public LiveData<Packet> getPacketLiveData(String packet_id) {
    this.packetLiveData = repository.getPacketLiveData(packet_id);
    return packetLiveData;
  }
  
  public LiveData<Util.Event<String>> getNotificationLiveData(){
    return notificationLiveData;
  }
  
  public void refreshCatering() {
    repository.refreshCatering();
  }
  
  public void uploadCateringAvatar(Catering catering, String filename, byte[] file){
    repository.uploadCateringAvatar(catering, filename, file);
  }
  
  public void updateCatering(Catering catering){
  	repository.updateCatering(catering);
	}
  
  public void refreshPackets() {
    repository.refreshPackets();
  }
  
  public void refreshPacket(String packet_id) {
    repository.refreshPacket(packet_id);
  }
  
  public void uploadPacketImage(Packet packet, String filename, byte[] file, int code){
    repository.uploadPacketImage(packet, filename, file, code-2);
  }
  
  public void addPacket(Packet packet) {
    repository.addPacket(packet);
  }
  
  public void updatePacket(Packet packet) {
    repository.updatePacket(packet);
  }
  
  public void deletePacket(Packet packet) {
  
  }
  
  public void logout(Catering catering){
    repository.logout(catering);
  }
}