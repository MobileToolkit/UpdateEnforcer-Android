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

    @NonNull
    public Result getResult() {
        if (null == result) {
            result = Result.OUTDATED;

            if (appApplicationId.equals(versionInfo.getLatestVersion().getApplicationId())) {
                if (0 != appVersion.compareTo(new DefaultArtifactVersion(versionInfo.getLatestVersion().getVersionName()))) {
                    result = Result.OUTDATED;
                } else {
                    result = Result.UP_TO_DATE;
                }
            }

            if (!Result.UP_TO_DATE.equals(result)) {
                for (Version version : versionInfo.getUnsupportedVersions()) {
                    if (appApplicationId.equals(version.getApplicationId())) {
                        int r = new DefaultArtifactVersion(version.getVersionName()).compareTo(appVersion);
                        if (0 <= new DefaultArtifactVersion(version.getVersionName()).compareTo(appVersion)) {
                            result = Result.UNSUPPORTED;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

}
