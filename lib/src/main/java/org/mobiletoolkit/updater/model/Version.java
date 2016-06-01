package org.mobiletoolkit.updater.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class Version {

    @SerializedName("application_id")
    @Expose
    private String applicationId;

    @SerializedName("version_name")
    @Expose
    private String versionName;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

}
