package io.cauliframes.masakbanyak_catering.di;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.cauliframes.masakbanyak_catering.R;

@Module
public class StorageModule {
  @Provides
  @Singleton
  public SharedPreferences providePreferences(Context context) {
    return context.getSharedPreferences(
        context.getString(R.string.app_preferences_key),
        Context.MODE_PRIVATE
    );
  }
  
  @Provides
  @Singleton
  public SharedPreferences.Editor providePreferencesEditor(SharedPreferences preferences) {
    return preferences.edit();
  }
}
