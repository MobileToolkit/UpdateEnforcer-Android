package org.mobiletoolkit.updateenforcer;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.mobiletoolkit.updateenforcer.model.Version;
import org.mobiletoolkit.updateenforcer.model.VersionInfo;

/**
 * Created by Sebastian Owodzin on 15/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class VersionChecker {

    private Activity activity;

    private DefaultArtifactVersion appVersion;

    private VersionInfo versionInfo;

    public enum Result {
        UNSUPPORTED, OUTDATED, UP_TO_DATE
    }

    public VersionChecker(@NonNull Activity activity, @NonNull String appVersionName, @NonNull VersionInfo versionInfo) {
        this.activity = activity;
        this.appVersion = new DefaultArtifactVersion(appVersionName);
        this.versionInfo = versionInfo;
    }

    public Result getResult() {
        Result result = Result.UP_TO_DATE;

        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(versionInfo.getLatestVersion().getVersionName());

        if (0 != appVersion.compareTo(latestVersion)) {
            result = Result.OUTDATED;
        }

        for (Version version : versionInfo.getUnsupportedVersions()) {
            if (0 != appVersion.compareTo(new DefaultArtifactVersion(version.getVersionName()))) {
                result = Result.UNSUPPORTED;
                break;
            }
        }

        return result;
    }

    public String getLatestVersionApplicationId() {
        return versionInfo.getLatestVersion().getApplicationId();
    }

}
