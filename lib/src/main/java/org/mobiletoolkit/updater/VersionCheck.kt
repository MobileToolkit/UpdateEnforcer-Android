package org.mobiletoolkit.updater

import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.mobiletoolkit.updater.model.VersionsInfo

/**
 * Created by Sebastian Owodzin on 15/05/2016.
 * Copyright © 2017 mobiletoolkit.org. All rights reserved.
 */
public class VersionCheck(
        private val applicationId: String,
        private val versionName: String,
        val versionsInfo: VersionsInfo
) {
    public enum class Result {
        UP_TO_DATE, OUTDATED, UNSUPPORTED
    }
    public val result: Result
        get() {
            var resultValue = Result.OUTDATED

            val appVersion: DefaultArtifactVersion = DefaultArtifactVersion(versionName)

            if (applicationId == versionsInfo.latestVersionData.applicationId) {
                if (appVersion < DefaultArtifactVersion(versionsInfo.latestVersionData.versionName)) {
                    resultValue = Result.OUTDATED
                } else {
                    resultValue = Result.UP_TO_DATE
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
