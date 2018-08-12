package io.cauliframes.masakbanyak_catering.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import io.cauliframes.masakbanyak_catering.R;
import io.cauliframes.masakbanyak_catering.di.Components;
import io.cauliframes.masakbanyak_catering.model.Packet;
import io.cauliframes.masakbanyak_catering.viewmodel.CateringViewModel;
import io.cauliframes.masakbanyak_catering.viewmodel.ViewModelFactory;

import static io.cauliframes.masakbanyak_catering.Constants.MASAKBANYAK_URL;
import static io.cauliframes.masakbanyak_catering.Constants.PICK_IMAGE_REQUEST_CODE;
import static io.cauliframes.masakbanyak_catering.Constants.PICK_PACKET_IMAGE_REQUEST_CODE_01;
import static io.cauliframes.masakbanyak_catering.Constants.PICK_PACKET_IMAGE_REQUEST_CODE_02;
import static io.cauliframes.masakbanyak_catering.Constants.PICK_PACKET_IMAGE_REQUEST_CODE_03;

public class EditPacketActivity extends AppCompatActivity {
  
  @Inject
  ViewModelFactory mViewModelFactory;
  
  CateringViewModel mCateringViewModel;
  
  private Packet mPacket;
  
  private CoordinatorLayout mCoordinatorLayout;
  private ImageView mImage01;
  private ImageView mImage02;
  private ImageView mImage03;
  private EditText mPacketNameInput;
  private EditText mPacketMinimumQuantityInput;
  private EditText mPacketPriceInput;
  private LinearLayout mPacketContentsLayout;
  private FloatingActionButton mUpdatePacketButton;
  private FloatingActionButton mMinusContentButton;
  private FloatingActionButton mPlusContentButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_packet);
    
    Components.getSessionComponent().inject(this);
    
    mCateringViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CateringViewModel.class);
    
    mPacket = (Packet) getIntent().getSerializableExtra("packet");
    
    mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
    mImage01 = findViewById(R.id.imageView1);
    mImage02 = findViewById(R.id.imageView2);
    mImage03 = findViewById(R.id.imageView3);
    mPacketNameInput = findViewById(R.id.packetNameEditText);
    mPacketMinimumQuantityInput = findViewById(R.id.minimumQuantityEditText);
    mPacketPriceInput = findViewById(R.id.priceEditText);
    mPacketContentsLayout = findViewById(R.id.packetContentsLayout);
    mUpdatePacketButton = findViewById(R.id.renewButton);
    mMinusContentButton = findViewById(R.id.minusButton);
    mPlusContentButton = findViewById(R.id.plusButton);
    
    mImage01.setOnClickListener(view -> {
      Intent intent = new Intent();
      intent.setType("image/jpeg");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PACKET_IMAGE_REQUEST_CODE_01);
    });
    
    mImage02.setOnClickListener(view -> {
      Intent intent = new Intent();
      intent.setType("image/jpeg");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PACKET_IMAGE_REQUEST_CODE_02);
    });
    
    mImage03.setOnClickListener(view -> {
      Intent intent = new Intent();
      intent.setType("image/jpeg");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PACKET_IMAGE_REQUEST_CODE_03);
    });
    
    mMinusContentButton.setOnClickListener(view -> {
      if (mPacketContentsLayout.getChildCount() != 0) {
        mPacketContentsLayout.removeViewAt(mPacketContentsLayout.getChildCount() - 1);
      }
      
      showResponse("Jumlah makanan/minuman: " + mPacketContentsLayout.getChildCount());
    });
    
    mPlusContentButton.setOnClickListener(view -> {
      View layoutView = getLayoutInflater().inflate(R.layout.itemview_packet_content, null, false);
      mPacketContentsLayout.addView(layoutView);
      
      showResponse("Jumlah makanan/minuman: " + mPacketContentsLayout.getChildCount());
    });
    
    mUpdatePacketButton.setOnClickListener(view -> {
      mPacket.setName(mPacketNameInput.getText().toString());
      mPacket.setMinimum_quantity(Integer.parseInt(mPacketMinimumQuantityInput.getText().toString()));
      mPacket.setPrice(Integer.parseInt(mPacketPriceInput.getText().toString()));
      
      ArrayList<String> packetContents = new ArrayList<>();
      for (int i = 0; i < mPacketContentsLayout.getChildCount(); i++) {
        View layoutView = mPacketContentsLayout.getChildAt(i);
        EditText packetContent = layoutView.findViewById(R.id.packetContentEditText);
        
        packetContents.add(packetContent.getText().toString());
      }
      mPacket.setContents(packetContents);
      
      mCateringViewModel.updatePacket(mPacket);
    });
    
    mCateringViewModel.getNotificationLiveData().observe(this, notificationEvent -> {
      String notification = notificationEvent.getContentIfNotHandled();
      
      if (notification != null) {
        showResponse(notification);
      }
    });
    
    for (int j = 0; j < mPacket.getImages().size(); j++) {
      switch (j) {
        case 0:
          Picasso.get().load(MASAKBANYAK_URL + mPacket.getImages().get(j))
              .fit()
              .centerCrop()
              .into(mImage01);
          break;
        
        case 1:
          Picasso.get().load(MASAKBANYAK_URL + mPacket.getImages().get(j))
              .fit()
              .centerCrop()
              .into(mImage02);
          break;
          
        case 2:
          Picasso.get().load(MASAKBANYAK_URL + mPacket.getImages().get(j))
              .fit()
              .centerCrop()
              .into(mImage03);
          break;
          
        default:
          break;
      }
      
    }
    
    mPacketNameInput.setText(mPacket.getName());
    mPacketMinimumQuantityInput.setText(Integer.toString(mPacket.getMinimum_quantity()));
    mPacketPriceInput.setText(Integer.toString(mPacket.getPrice()));
    
    for (int k = 0; k < mPacket.getContents().size(); k++) {
      View layoutView = getLayoutInflater().inflate(R.layout.itemview_packet_content, null, false);
      EditText contentInput = layoutView.findViewById(R.id.packetContentEditText);
      
      contentInput.setText(mPacket.getContents().get(k));
      
      mPacketContentsLayout.addView(layoutView);
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (requestCode == PICK_PACKET_IMAGE_REQUEST_CODE_01 && resultCode == RESULT_OK &&
        data != null && data.getData() != null) {
      
      String filename = "";
      byte[] file = "".getBytes();
      
      //get uri from the result given
      Uri uri = data.getData();
      
      //get filename with content resolver
      String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
      Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
      
      if (cursor != null && cursor.getCount() > 0) {
        int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        filename = cursor.getString(columnIndex);
      }
      
      cursor.close();
      
      //get file with content resolver
      try {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        file = ByteStreams.toByteArray(inputStream);
        inputStream.close();
      } catch (Exception e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
      }
      
      //execute avatar upload
      mCateringViewModel.uploadPacketImage(mPacket, filename, file, PICK_PACKET_IMAGE_REQUEST_CODE_01);
    }
    
    if (requestCode == PICK_PACKET_IMAGE_REQUEST_CODE_02 && resultCode == RESULT_OK &&
        data != null && data.getData() != null) {
      
      String filename = "";
      byte[] file = "".getBytes();
      
      //get uri from the result given
      Uri uri = data.getData();
      
      //get filename with content resolver
      String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
      Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
      
      if (cursor != null && cursor.getCount() > 0) {
        int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        filename = cursor.getString(columnIndex);
      }
      
      cursor.close();
      
      //get file with content resolver
      try {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        file = ByteStreams.toByteArray(inputStream);
        inputStream.close();
      } catch (Exception e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
      }
      
      //execute avatar upload
      mCateringViewModel.uploadPacketImage(mPacket, filename, file, PICK_PACKET_IMAGE_REQUEST_CODE_02);
    }
    
    if (requestCode == PICK_PACKET_IMAGE_REQUEST_CODE_03 && resultCode == RESULT_OK &&
        data != null && data.getData() != null) {
      
      String filename = "";
      byte[] file = "".getBytes();
      
      //get uri from the result given
      Uri uri = data.getData();
      
      //get filename with content resolver
      String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
      Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
      
      if (cursor != null && cursor.getCount() > 0) {
        int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        filename = cursor.getString(columnIndex);
      }
      
      cursor.close();
      
      //get file with content resolver
      try {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        file = ByteStreams.toByteArray(inputStream);
        inputStream.close();
      } catch (Exception e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
      }
      
      //execute avatar upload
      mCateringViewModel.uploadPacketImage(mPacket, filename, file, PICK_PACKET_IMAGE_REQUEST_CODE_03);
    }
  }
  
  public void showResponse(String response) {
    Snackbar.make(mCoordinatorLayout, response, Snackbar.LENGTH_SHORT).show();
  }
}
