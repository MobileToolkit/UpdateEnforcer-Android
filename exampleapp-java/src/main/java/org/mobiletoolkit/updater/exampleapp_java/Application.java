package org.mobiletoolkit.updater.exampleapp_java;

import android.support.annotation.NonNull;
import android.util.Log;
import org.mobiletoolkit.updater.VersionCheck;
import org.mobiletoolkit.updater.model.VersionsInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sebastian Owodzin on 11/11/2017.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VersionInfoApi versionInfoService = retrofit.create(VersionInfoApi.class);

        versionInfoService.versionsInfo().enqueue(new Callback<VersionsInfo>() {

            @Override
            public void onResponse(@NonNull Call<VersionsInfo> call, @NonNull Response<VersionsInfo> response) {
                Log.v("VersionInfoApi", "Callback::onResponse response: " + response);
                Log.v("VersionInfoApi", "Callback::onResponse body: " + response.body());

                if (response.body() != null) {
                    VersionCheck.Result result = new VersionCheck(BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID, response.body()).getResult();

                    Log.v("VersionCheck", "result: " + result);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VersionsInfo> call, @NonNull Throwable t) {
                Log.v("VersionInfoApi", "Callback::onFailure error: " + t);

                // VersionsInfo fetch was unsuccessful, continue with the app launch
            }
        });
    }
}
