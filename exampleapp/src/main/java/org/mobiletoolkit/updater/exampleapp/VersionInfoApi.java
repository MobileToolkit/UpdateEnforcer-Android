package org.mobiletoolkit.updater.exampleapp;

import org.mobiletoolkit.updater.model.VersionInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public interface VersionInfoApi {

    @GET("/v2/573cb455110000de22aa8b2a")
    Call<VersionInfo> getVersionInfo();

}
