package org.mobiletoolkit.updater

import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.mobiletoolkit.updater.model.VersionsInfo

/**
 * Created by Sebastian Owodzin on 15/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
class VersionCheck(
        private val applicationId: String,
        private val versionName: String,
        val versionsInfo: VersionsInfo
) {
    enum class Result {
        UP_TO_DATE, OUTDATED, UNSUPPORTED
    }

    val result: Result
        get() {
            var resultValue = Result.OUTDATED

            val appVersion = DefaultArtifactVersion(versionName)

            if (applicationId == versionsInfo.latestVersionData.applicationId) {
                resultValue = if (appVersion < DefaultArtifactVersion(versionsInfo.latestVersionData.versionName)) {
                    Result.OUTDATED
                } else {
                    Result.UP_TO_DATE
                }
            }

            if (resultValue != Result.UP_TO_DATE) {
                if (versionsInfo.unsupportedVersions.filter { it.applicationId == applicationId && DefaultArtifactVersion(it.versionName) >= appVersion }.isNotEmpty()) {
                    resultValue = Result.UNSUPPORTED
                }
            }

            return resultValue
        }
}
