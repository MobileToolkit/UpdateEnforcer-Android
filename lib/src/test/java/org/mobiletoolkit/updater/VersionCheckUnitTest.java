package org.mobiletoolkit.updater;

import com.google.gson.Gson;

import org.junit.Test;
import org.mobiletoolkit.updater.model.VersionInfo;

import java.io.InputStreamReader;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class VersionCheckUnitTest {
    @Test
    public void getResult() {
        final VersionInfo versionInfo = new Gson().fromJson(new InputStreamReader(getClass().getResourceAsStream("version_info.json")), VersionInfo.class);

        HashMap<VersionCheck.Result, VersionCheck.Result> versionCheckResults = new HashMap<VersionCheck.Result, VersionCheck.Result>() {{
            put(VersionCheck.Result.UP_TO_DATE,  new VersionCheck("2.0.1", "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.UP_TO_DATE,  new VersionCheck("2.0",   "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.OUTDATED,    new VersionCheck("1.6",   "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("1.5",   "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.9",   "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.4.5", "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.3",   "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.2",   "org.mobiletoolkit.updater.exampleapp",    versionInfo).getResult());

            put(VersionCheck.Result.OUTDATED,    new VersionCheck("2.0.1", "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.OUTDATED,    new VersionCheck("2.0",   "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.OUTDATED,    new VersionCheck("1.6",   "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.OUTDATED,    new VersionCheck("1.5",   "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.OUTDATED,    new VersionCheck("0.9",   "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.4.5", "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.3",   "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
            put(VersionCheck.Result.UNSUPPORTED, new VersionCheck("0.2",   "org.mobiletoolkit.updater.exampleapp.old", versionInfo).getResult());
        }};

        assertArrayEquals(versionCheckResults.keySet().toArray(), versionCheckResults.values().toArray());
    }
}