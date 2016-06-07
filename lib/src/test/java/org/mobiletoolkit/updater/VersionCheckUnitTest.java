package org.mobiletoolkit.updater;

import com.google.gson.Gson;

import org.junit.Test;
import org.mobiletoolkit.updater.model.VersionInfo;

import java.io.InputStreamReader;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class VersionCheckUnitTest {
    @Test
    public void getResult() {
        VersionInfo versionInfo = new Gson().fromJson(new InputStreamReader(getClass().getResourceAsStream("version_info.json")), VersionInfo.class);

        VersionCheck.Result[] expecteds, actuals;

        expecteds = new VersionCheck.Result[] {
                VersionCheck.Result.UP_TO_DATE,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.OUTDATED,
                VersionCheck.Result.OUTDATED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.OUTDATED
        };

        String appId = "org.mobiletoolkit.updater.exampleapp";
        String oldAppId = "org.mobiletoolkit.updater.exampleapp.old";

        actuals = new VersionCheck.Result[] {
                new VersionCheck("2.0",   appId,    versionInfo).getResult(),
                new VersionCheck("0.2",   appId,    versionInfo).getResult(),
                new VersionCheck("0.4.5", appId,    versionInfo).getResult(),
                new VersionCheck("0.9",   appId,    versionInfo).getResult(),
                new VersionCheck("1.6",   appId,    versionInfo).getResult(),
                new VersionCheck("2.0",   oldAppId, versionInfo).getResult(),
                new VersionCheck("0.2",   oldAppId, versionInfo).getResult(),
                new VersionCheck("0.4.5", oldAppId, versionInfo).getResult(),
                new VersionCheck("0.9",   oldAppId, versionInfo).getResult(),
                new VersionCheck("1.6",   oldAppId, versionInfo).getResult()
        };

        assertArrayEquals(expecteds, actuals);
    }
}