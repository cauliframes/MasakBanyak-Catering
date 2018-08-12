package io.cauliframes.masakbanyak_catering.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import io.cauliframes.masakbanyak_catering.R;
import io.cauliframes.masakbanyak_catering.di.Components;
import io.cauliframes.masakbanyak_catering.model.Packet;
import io.cauliframes.masakbanyak_catering.viewmodel.CateringViewModel;
import io.cauliframes.masakbanyak_catering.viewmodel.ViewModelFactory;

public class AddPacketActivity extends AppCompatActivity {
  
  @Inject
  ViewModelFactory mViewModelFactory;
  
  CateringViewModel mCateringViewModel;
  
  private Packet mPacket;
  
  private CoordinatorLayout mCoordinatorLayout;
  private EditText mPacketNameInput;
  private EditText mPacketMinimumQuantityInput;
  private EditText mPacketPriceInput;
  private LinearLayout mPacketContentsLayout;
  private Button mAddPacketButton;
  private FloatingActionButton mMinusContentButton;
  private FloatingActionButton mPlusContentButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_packet);
  
    Components.getSessionComponent().inject(this);
    
    mCateringViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CateringViewModel.class);
    
    mPacket = new Packet();
    
    mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
    mPacketNameInput = findViewById(R.id.packetNameEditText);
    mPacketMinimumQuantityInput = findViewById(R.id.minimumQuantityEditText);
    mPacketPriceInput = findViewById(R.id.priceEditText);
    mPacketContentsLayout = findViewById(R.id.packetContentsLayout);
    mAddPacketButton = findViewById(R.id.addPacketButton);
    mMinusContentButton = findViewById(R.id.minusButton);
    mPlusContentButton = findViewById(R.id.plusButton);
    
    mMinusContentButton.setOnClickListener(view -> {
      if(mPacketContentsLayout.getChildCount() != 0){
        mPacketContentsLayout.removeViewAt(mPacketContentsLayout.getChildCount()-1);
      }
  
      showResponse("Jumlah makanan/minuman: "+mPacketContentsLayout.getChildCount());
    });
    
    mPlusContentButton.setOnClickListener(view -> {
      View layoutView = getLayoutInflater().inflate(R.layout.itemview_packet_content, null, false);
      
      mPacketContentsLayout.addView(layoutView);
      
      showResponse("Jumlah makanan/minuman: "+mPacketContentsLayout.getChildCount());
    });
    
    mAddPacketButton.setOnClickListener(view -> {
      mPacket.setName(mPacketNameInput.getText().toString());
      mPacket.setMinimum_quantity(Integer.parseInt(mPacketMinimumQuantityInput.getText().toString()));
      mPacket.setPrice(Integer.parseInt(mPacketPriceInput.getText().toString()));
  
      ArrayList<String> packetContents = new ArrayList<>();
      for(int i = 0; i < mPacketContentsLayout.getChildCount(); i++){
        View layoutView = mPacketContentsLayout.getChildAt(i);
        EditText packetContent = layoutView.findViewById(R.id.packetContentEditText);
        
        packetContents.add(packetContent.getText().toString());
      }
      mPacket.setContents(packetContents);
      
      mCateringViewModel.addPacket(mPacket);
    });
    
    mCateringViewModel.getNotificationLiveData().observe(this, notificationEvent -> {
      String notification = notificationEvent.getContentIfNotHandled();
  
      if (notification != null) {
        showResponse(notification);
      }
    });
  }
  
  public void showResponse(String response){
    Snackbar.make(mCoordinatorLayout, response, Snackbar.LENGTH_SHORT).show();
  }
}
