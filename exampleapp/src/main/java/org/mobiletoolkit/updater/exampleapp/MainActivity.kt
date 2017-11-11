package org.mobiletoolkit.updater.exampleapp

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.mobiletoolkit.updater.SimpleCallback

import org.mobiletoolkit.updater.Updater
import org.mobiletoolkit.updater.VersionCheck
import org.mobiletoolkit.updater.exampleapp.databinding.ActivityMainBinding
import org.mobiletoolkit.updater.model.VersionsInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var versionInfoService: VersionInfoApi

    private val updaterCallback = object : SimpleCallback() {
        override fun onLatestVersionLaunchCancelled(versionCheckResult: VersionCheck.Result) {
            if (VersionCheck.Result.UNSUPPORTED == versionCheckResult) {
                Toast.makeText(this@MainActivity, "UNSUPPORTED app version, launch should be stopped", Toast.LENGTH_LONG).show()
            }
        }

//        override fun onVersionUpdateStarted(versionCheckResult: VersionCheck.Result) {
//            // should wait with app launch until mobiletoolkit_updater_update_button is finished
//        }
//
//        override fun onVersionUpdateCancelled(versionCheckResult: VersionCheck.Result) {
//            if (VersionCheck.Result.UNSUPPORTED == versionCheckResult) {
//                // app launch should be stopped
//            } else {
//                // continue with the app launch
//            }
//        }
//
//        override fun onUninstallUnsupportedVersionsStarted() {
//            // wait with app launch until it's finished
//        }
//
//        override fun onUninstallUnsupportedVersionsSkipped() {
//            // continue with the app launch
//        }
//
//        override fun onUninstallUnsupportedVersionsFinished() {
//            // continue with the app launch
//        }
//
//        override fun onUninstallUnsupportedVersionStarted(applicationId: String) {
//            // continue with the app launch
//        }
//
//        override fun onUninstallUnsupportedVersionCancelled(applicationId: String) {
//            // continue with the app launch
//        }
    }

    private val updater = Updater(this, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, callback = updaterCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        versionInfoService = Retrofit.Builder()
                .baseUrl("http://www.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VersionInfoApi::class.java)

        binding.textView.text = "${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}"
    }

    override fun onResume() {
        super.onResume()

        versionInfoService.versionsInfo.enqueue(object : Callback<VersionsInfo> {
            override fun onFailure(call: Call<VersionsInfo>?, t: Throwable?) {
                Log.i("VersionInfoApi", "Callback::onFailure error: $t")

                // VersionsInfo fetch was unsuccessful, do nothing
            }

            override fun onResponse(call: Call<VersionsInfo>?, response: Response<VersionsInfo>?) {
                Log.i("VersionInfoApi", "Callback::onResponse response: $response")
                Log.i("VersionInfoApi", "Callback::onResponse body: ${response?.body()}")

                if (response?.body() != null) {
                    updater.execute(response.body() as VersionsInfo)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (updater.onActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
