package org.mobiletoolkit.updater.exampleapp_java;

import org.mobiletoolkit.updater.model.VersionsInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sebastian Owodzin on 12/11/2017.
 */

public interface VersionInfoApi {

    @GET("/v2/5a0778072f00002f0ae610d9")
    Call<VersionsInfo> versionsInfo();
}
