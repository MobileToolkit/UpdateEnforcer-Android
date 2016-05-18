package org.mobiletoolkit.updateenforcer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class VersionInfo {

    @SerializedName("latest_version")
    @Expose
    private Version latestVersion;

    @SerializedName("unsupported_versions")
    @Expose
    private List<Version> unsupportedVersions = new ArrayList<>();

    @SerializedName("uninstall_unsupported_versions")
    @Expose
    private Boolean uninstallUnsupportedVersions = false;

    @SerializedName("unsupported_version_update_prompt")
    @Expose
    private UpdatePrompt unsupportedVersionUpdatePrompt;

    @SerializedName("outdated_version_update_prompt")
    @Expose
    private UpdatePrompt outdatedVersionUpdatePrompt;

    public Version getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Version latestVersion) {
        this.latestVersion = latestVersion;
    }

    public List<Version> getUnsupportedVersions() {
        return unsupportedVersions;
    }

    public void setUnsupportedVersions(List<Version> unsupportedVersions) {
        this.unsupportedVersions = unsupportedVersions;
    }

    public Boolean getUninstallUnsupportedVersions() {
        return uninstallUnsupportedVersions;
    }

    public void setUninstallUnsupportedVersions(Boolean uninstallUnsupportedVersions) {
        this.uninstallUnsupportedVersions = uninstallUnsupportedVersions;
    }

    public UpdatePrompt getUnsupportedVersionUpdatePrompt() {
        return unsupportedVersionUpdatePrompt;
    }

    public void setUnsupportedVersionUpdatePrompt(UpdatePrompt unsupportedVersionUpdatePrompt) {
        this.unsupportedVersionUpdatePrompt = unsupportedVersionUpdatePrompt;
    }

    public UpdatePrompt getOutdatedVersionUpdatePrompt() {
        return outdatedVersionUpdatePrompt;
    }

    public void setOutdatedVersionUpdatePrompt(UpdatePrompt outdatedVersionUpdatePrompt) {
        this.outdatedVersionUpdatePrompt = outdatedVersionUpdatePrompt;
    }

}
