package org.mobiletoolkit.updateenforcer.exampleapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.mobiletoolkit.updateenforcer.Enforcer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VersionInfoApiEndpoint versionInfoApiEndpoint = retrofit.create(VersionInfoApiEndpoint.class);

        Call<VersionInfo> call = versionInfoApiEndpoint.getVersionInfo();
        call.enqueue(new Callback<VersionInfo>() {
            @Override
            public void onResponse(Call<VersionInfo> call, Response<VersionInfo> response) {
                new Enforcer(MainActivity.this, BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID, response.body()).run();
            }

            @Override
            public void onFailure(Call<VersionInfo> call, Throwable t) {

            }
        });

    }
}
