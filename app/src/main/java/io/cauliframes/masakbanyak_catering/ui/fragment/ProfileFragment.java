package io.cauliframes.masakbanyak_catering.ui.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.cauliframes.masakbanyak_catering.R;
import io.cauliframes.masakbanyak_catering.di.Components;
import io.cauliframes.masakbanyak_catering.model.Catering;
import io.cauliframes.masakbanyak_catering.viewmodel.CateringViewModel;
import io.cauliframes.masakbanyak_catering.viewmodel.ViewModelFactory;
import me.itangqi.waveloadingview.WaveLoadingView;

import static android.app.Activity.RESULT_OK;
import static io.cauliframes.masakbanyak_catering.Constants.MASAKBANYAK_URL;
import static io.cauliframes.masakbanyak_catering.Constants.PICK_IMAGE_REQUEST_CODE;

public class ProfileFragment extends Fragment {
  
  @Inject
  ViewModelFactory mViewModelFactory;
  
  private CateringViewModel mCateringViewModel;
  
  private Catering mCatering;
  
  private CoordinatorLayout mCoordinatorLayout;
  private ConstraintLayout mParentLayout;
  private SwipeRefreshLayout mRefreshLayout;
  private CircleImageView mCateringAvatarImage;
  private EditText mCateringNameInput;
  private EditText mCateringAddressInput;
  private EditText mCateringPhoneInput;
  private TextView mCateringEmailText;
  private Button mUpdateCateringButton;
  private Button mLogoutButton;
  private AlertDialog mLogoutDialog;
  
  private OnFragmentInteractionListener mListener;
  
  public ProfileFragment() {
  
  }
  
  public static ProfileFragment newInstance() {
    ProfileFragment fragment = new ProfileFragment();
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    Components.getSessionComponent().inject(this);
    
    mCateringViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CateringViewModel.class);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    
    mCoordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
    mParentLayout = view.findViewById(R.id.constraintLayout);
    mRefreshLayout = view.findViewById(R.id.refreshLayout);
    mCateringAvatarImage = view.findViewById(R.id.cateringAvatarImageView);
    mCateringNameInput = view.findViewById(R.id.cateringName);
    mCateringAddressInput = view.findViewById(R.id.cateringAddress);
    mCateringPhoneInput = view.findViewById(R.id.cateringPhoneNumber);
    mCateringEmailText = view.findViewById(R.id.cateringEmail);
    mUpdateCateringButton = view.findViewById(R.id.updateCateringButton);
    mLogoutButton = view.findViewById(R.id.logoutButton);
    
    initializeLogoutDialog();
    
    return view;
  }
  
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  
    mRefreshLayout.setRefreshing(true);
    mRefreshLayout.setOnRefreshListener(mCateringViewModel::refreshCatering);
    
    mCateringAvatarImage.setOnClickListener(v -> {
      Intent intent = new Intent();
      intent.setType("image/jpeg");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE);
    });
    
    mUpdateCateringButton.setOnClickListener(view1 -> {
      mCatering.setName(mCateringNameInput.getText().toString());
      mCatering.setAddress(mCateringAddressInput.getText().toString());
      mCatering.setPhone(mCateringPhoneInput.getText().toString());
      
      mCateringViewModel.updateCatering(mCatering);
    });
    
    mLogoutButton.setOnClickListener(view2 -> mLogoutDialog.show());
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    
    mCateringViewModel.getNotificationLiveData().observe(this, notificationEvent -> {
      String notification = notificationEvent.getContentIfNotHandled();
      if (notification != null) {
        Snackbar.make(mCoordinatorLayout, notification, Snackbar.LENGTH_SHORT).show();
      }
    });
    
    mCateringViewModel.getCateringLiveData().observe(this, catering -> {
      mCatering = catering;
      
      Picasso.get().load(MASAKBANYAK_URL + mCatering.getAvatar()).fit().centerCrop().into(mCateringAvatarImage);
      
      mCateringNameInput.setText(catering.getName());
      mCateringAddressInput.setText(catering.getAddress());
      mCateringPhoneInput.setText(catering.getPhone());
      mCateringEmailText.setText(catering.getEmail());
      
      mRefreshLayout.setRefreshing(false);
    });
  }
  
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    
    mCateringViewModel.getCateringLiveData().removeObservers(this);
  }
  
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
        data != null && data.getData() != null) {
      
      //initialize file and filename
      String filename = "";
      byte[] file = "".getBytes();
      
      //get uri
      Uri uri = data.getData();
      
      //get filename with content resolver
      String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
      Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
      
      if (cursor != null && cursor.getCount() > 0) {
        int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        filename = cursor.getString(columnIndex);
      }
      
      cursor.close();
      
      //get file with content resolver
      try {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        file = ByteStreams.toByteArray(inputStream);
        inputStream.close();
      } catch (Exception e) {
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
      }
      
      //execute avatar upload
      mCateringViewModel.uploadCateringAvatar(mCatering, filename, file);
    }
  }
  
  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
  
  public void initializeLogoutDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    
    builder.setMessage("Apakah anda yakin ingin keluar?")
        .setTitle("Keluar Dari Akun.")
        .setNegativeButton("Tidak", null)
        .setPositiveButton("Ya", (dialogInterface, i) -> {
          mRefreshLayout.setRefreshing(true);
          mCateringViewModel.logout(mCatering);
          dialogInterface.dismiss();
        });
    
    mLogoutDialog = builder.create();
  }
}