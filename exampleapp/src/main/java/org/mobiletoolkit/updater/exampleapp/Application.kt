package org.mobiletoolkit.updater.exampleapp

import android.app.Application
import android.util.Log
import org.mobiletoolkit.updater.VersionCheck
import org.mobiletoolkit.updater.model.VersionsInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Sebastian Owodzin on 21/05/2017.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://www.mocky.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val versionInfoService = retrofit.create(VersionInfoApi::class.java)

        versionInfoService.versionsInfo.enqueue(object : Callback<VersionsInfo> {

            override fun onResponse(call: Call<VersionsInfo>?, response: Response<VersionsInfo>?) {
                Log.v("VersionInfoApi", "Callback::onResponse response: $response")
                Log.v("VersionInfoApi", "Callback::onResponse body: ${response?.body()}")

                if (response?.body() != null) {
                    val result = VersionCheck(BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID, response.body() as VersionsInfo).result

                    Log.v("VersionCheck", "result: $result")
                }
            }

            override fun onFailure(call: Call<VersionsInfo>?, t: Throwable?) {
                Log.v("VersionInfoApi", "Callback::onFailure error: $t")

                // VersionsInfo fetch was unsuccessful, continue with the app launch
            }
        })
    }
}