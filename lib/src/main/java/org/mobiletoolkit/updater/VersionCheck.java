package org.mobiletoolkit.updater;

import android.support.annotation.NonNull;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.mobiletoolkit.updater.model.Version;
import org.mobiletoolkit.updater.model.VersionInfo;

/**
 * Created by Sebastian Owodzin on 15/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class VersionCheck {

    private DefaultArtifactVersion appVersion;
    private String appApplicationId;

    private VersionInfo versionInfo;

    private Result result = null;

    public enum Result {
        UNSUPPORTED, OUTDATED, UP_TO_DATE
    }

    public VersionCheck(@NonNull String appVersionName, @NonNull String appApplicationId, @NonNull VersionInfo versionInfo) {
        this.appVersion = new DefaultArtifactVersion(appVersionName);
        this.appApplicationId = appApplicationId;
        this.versionInfo = versionInfo;
    }

    public Result getResult() {
        if (null == result) {
            result = Result.UP_TO_DATE;

            if (appApplicationId.equals(versionInfo.getLatestVersion().getApplicationId())) {
                DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(versionInfo.getLatestVersion().getVersionName());
                if (0 != appVersion.compareTo(latestVersion)) {
                    result = Result.OUTDATED;
                }
            } else {
                result = Result.OUTDATED;
            }

            for (Version version : versionInfo.getUnsupportedVersions()) {
                if (0 < new DefaultArtifactVersion(version.getVersionName()).compareTo(appVersion)) {
                    result = Result.UNSUPPORTED;
                    break;
                }
            }
        }

        return result;
    }

}
