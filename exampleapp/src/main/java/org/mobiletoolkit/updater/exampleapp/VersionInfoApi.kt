package org.mobiletoolkit.updater.exampleapp

import org.mobiletoolkit.updater.model.VersionsInfo

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
interface VersionInfoApi {

    @get:GET("/v2/5a0778072f00002f0ae610d9")
    val versionsInfo: Call<VersionsInfo>
}
