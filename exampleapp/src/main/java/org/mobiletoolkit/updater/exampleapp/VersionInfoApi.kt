package org.mobiletoolkit.updater.exampleapp

import org.mobiletoolkit.updater.model.VersionsInfo

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
internal interface VersionInfoApi {
    @get:GET("/v2/592184143700008203fa33ff")
    val versionsInfo: Call<VersionsInfo>

}
