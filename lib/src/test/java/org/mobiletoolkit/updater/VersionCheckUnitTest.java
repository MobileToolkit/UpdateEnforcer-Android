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

        String appId = "org.mobiletoolkit.exampleapp";

        expecteds = new VersionCheck.Result[] {
                VersionCheck.Result.UP_TO_DATE,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.OUTDATED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED,
                VersionCheck.Result.UNSUPPORTED
        };

        actuals = new VersionCheck.Result[] {
                new VersionCheck("2.0", "org.mobiletoolkit.exampleapp", versionInfo).getResult(),
                new VersionCheck("0.2", "org.mobiletoolkit.exampleapp", versionInfo).getResult(),
                new VersionCheck("0.4.5", "org.mobiletoolkit.exampleapp", versionInfo).getResult(),
                new VersionCheck("0.9", "org.mobiletoolkit.exampleapp", versionInfo).getResult(),
                new VersionCheck("1.6", "org.mobiletoolkit.exampleapp", versionInfo).getResult(),
                new VersionCheck("2.0", "org.mobiletoolkit.example.app.old", versionInfo).getResult(),
                new VersionCheck("0.2", "org.mobiletoolkit.example.app.old", versionInfo).getResult(),
                new VersionCheck("0.4.5", "org.mobiletoolkit.example.app.old", versionInfo).getResult(),
                new VersionCheck("0.9", "org.mobiletoolkit.example.app.old", versionInfo).getResult(),
                new VersionCheck("1.6", "org.mobiletoolkit.example.app.old", versionInfo).getResult()
        };

        assertArrayEquals(expecteds, actuals);
    }
}