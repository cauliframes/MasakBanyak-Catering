package io.cauliframes.masakbanyak_catering.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.cauliframes.masakbanyak_catering.Constants;
import io.cauliframes.masakbanyak_catering.webservice.MasakBanyakWebService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module()
public class NetworkModule {
  @Provides
  @Singleton
  public Retrofit provideRetrofit() {
    return new Retrofit.Builder()
        .baseUrl(Constants.MASAKBANYAK_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }
  
  @Provides
  @Singleton
  public MasakBanyakWebService provideWebService(Retrofit retrofit) {
    return retrofit.create(MasakBanyakWebService.class);
  }
}
