package org.mobiletoolkit.updater.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public data class VersionData(
        @SerializedName("application_id") val applicationId: String,
        @SerializedName("version_name") val versionName: String
)
