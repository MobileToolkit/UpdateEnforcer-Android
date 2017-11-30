package org.mobiletoolkit.updater.exampleapp_java;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.mobiletoolkit.updater.Callback;
import org.mobiletoolkit.updater.SimpleCallback;
import org.mobiletoolkit.updater.Updater;
import org.mobiletoolkit.updater.VersionCheck;
import org.mobiletoolkit.updater.exampleapp_java.databinding.ActivityMainBinding;
import org.mobiletoolkit.updater.model.VersionsInfo;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private VersionInfoApi versionInfoService;

    private Callback updaterCallback = new SimpleCallback() {

        @Override
        public void onLatestVersionLaunchCancelled(@NonNull VersionCheck.Result versionCheckResult) {
            if (VersionCheck.Result.UNSUPPORTED == versionCheckResult) {
                Toast.makeText(MainActivity.this, "UNSUPPORTED app version, launch should be stopped", Toast.LENGTH_LONG).show();
            }
        }

//        @Override
//        public void onVersionUpdateStarted(@NonNull VersionCheck.Result versionCheckResult) {
//            // should wait with app launch until mobiletoolkit_updater_update_button is finished
//        }
//
//        @Override
//        public void onVersionUpdateCancelled(@NonNull VersionCheck.Result versionCheckResult) {
//            if (VersionCheck.Result.UNSUPPORTED == versionCheckResult) {
//                // app launch should be stopped
//            } else {
//                // continue with the app launch
//            }
//        }
//
//        @Override
//        public void onUninstallUnsupportedVersionsStarted() {
//            // wait with app launch until it's finished
//        }
//
//        @Override
//        public void onUninstallUnsupportedVersionsSkipped() {
//            // continue with the app launch
//        }
//
//        @Override
//        public void onUninstallUnsupportedVersionsFinished() {
//            // continue with the app launch
//        }
//
//        @Override
//        public void onUninstallUnsupportedVersionStarted(@NonNull String applicationId) {
//            // continue with the app launch
//        }
//
//        @Override
//        public void onUninstallUnsupportedVersionCancelled(@NonNull String applicationId) {
//            // continue with the app launch
//        }
    };

    private Updater updater = new Updater(this, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, true, false, updaterCallback);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        versionInfoService = new Retrofit.Builder()
                .baseUrl("http://www.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VersionInfoApi.class);

        binding.textView.setText(BuildConfig.APPLICATION_ID + " - " + BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        versionInfoService.versionsInfo().enqueue(new retrofit2.Callback<VersionsInfo>() {

            @Override
            public void onResponse(@NonNull Call<VersionsInfo> call, @NonNull Response<VersionsInfo> response) {
                Log.v("VersionInfoApi", "Callback::onResponse response: " + response);
                Log.v("VersionInfoApi", "Callback::onResponse body: " + response.body());

                if (response.body() != null) {
                    updater.execute(response.body());
                } else {
                    // continue with the app launch
                }
            }

            @Override
            public void onFailure(@NonNull Call<VersionsInfo> call, @NonNull Throwable t) {
                Log.v("VersionInfoApi", "Callback::onFailure error: " + t);

                // VersionsInfo fetch was unsuccessful, continue with the app launch
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (updater.onActivityResult(requestCode, resultCode)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
