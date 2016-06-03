package org.mobiletoolkit.updater.exampleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.mobiletoolkit.updater.Updater;
import org.mobiletoolkit.updater.VersionCheck;
import org.mobiletoolkit.updater.model.VersionInfo;

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

    private Updater updater = new Updater(MainActivity.this, BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID);
    private VersionCheck.Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.textView);
        if (null != textView) {
            textView.setText(String.format("%s v%s", BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME));
        }

        updater.setListener(new Updater.Listener() {
            @Override
            public void latestVersionLaunchCancelled() {
                if (VersionCheck.Result.UNSUPPORTED.equals(result)) {
                    // app launch should be stopped
                }
            }

            @Override
            public void outdatedVersionUpdateStarted() {

            }

            @Override
            public void outdatedVersionUpdateCancelled() {

            }

            @Override
            public void unsupportedVersionUpdateStarted() {

            }
        });
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
                result = updater.run(response.body());

                if (VersionCheck.Result.UNSUPPORTED.equals(result)) {
                    // app launch should be stopped
                }
            }

            @Override
            public void onFailure(Call<VersionInfo> call, Throwable t) {
                // couldn't fetch VersionInfo, continue with the app launch
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (updater.handleOnActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
