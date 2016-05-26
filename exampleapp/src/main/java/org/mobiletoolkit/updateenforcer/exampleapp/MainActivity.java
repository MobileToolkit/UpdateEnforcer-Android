package org.mobiletoolkit.updateenforcer.exampleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.mobiletoolkit.updateenforcer.Updater;
import org.mobiletoolkit.updateenforcer.VersionCheck;
import org.mobiletoolkit.updateenforcer.model.VersionInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class MainActivity extends AppCompatActivity {

    private Updater updater;

    private VersionCheck.Result result = VersionCheck.Result.UP_TO_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updater = new Updater(MainActivity.this, BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VersionInfoApi versionInfoApi = retrofit.create(VersionInfoApi.class);

        Call<VersionInfo> call = versionInfoApi.getVersionInfo();
        call.enqueue(new Callback<VersionInfo>() {
            @Override
            public void onResponse(Call<VersionInfo> call, Response<VersionInfo> response) {
                updater.setVersionInfo(response.body());
                result = updater.run();

                if (!result.equals(VersionCheck.Result.UNSUPPORTED)) {
                    // app launch can only be continued when user is using OUTDATED or UP_TO_DATE version
                }
            }

            @Override
            public void onFailure(Call<VersionInfo> call, Throwable t) {
                // continue with the app launch
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != updater && updater.handleOnActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
