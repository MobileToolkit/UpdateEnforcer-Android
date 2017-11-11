package org.mobiletoolkit.updater.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Sebastian Owodzin on 02/04/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
data class UpdatePromptData(
        @SerializedName("title")
        val title: String,

        @SerializedName("message")
        val message: String
)
