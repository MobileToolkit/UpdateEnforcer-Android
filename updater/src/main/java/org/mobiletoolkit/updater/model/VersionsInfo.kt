package org.mobiletoolkit.updater.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
data class VersionsInfo(
        @SerializedName("latest_version")
        val latestVersionData: VersionData,

        @SerializedName("unsupported_versions")
        val unsupportedVersions: List<VersionData> = listOf(),

        @SerializedName("uninstall_unsupported_versions")
        val uninstallUnsupportedVersions: Boolean = false,

        @SerializedName("unsupported_version_update_prompt")
        val unsupportedVersionUpdatePromptData: UpdatePromptData? = null,

        @SerializedName("outdated_version_update_prompt")
        val outdatedVersionUpdatePromptData: UpdatePromptData? = null
)
