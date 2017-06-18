package org.mobiletoolkit.updater

/**
 * Created by Sebastian Owodzin on 17/06/2017.
 */
public abstract class SimpleCallback : Callback {
    override fun onLatestVersionLaunchCancelled(versionCheckResult: VersionCheck.Result) {}

    override fun onVersionUpdateStarted(versionCheckResult: VersionCheck.Result) {}
    override fun onVersionUpdateCancelled(versionCheckResult: VersionCheck.Result) {}

    override fun onUninstallUnsupportedVersionsStarted() {}
    override fun onUninstallUnsupportedVersionsSkipped() {}
    override fun onUninstallUnsupportedVersionsFinished() {}

    override fun onUninstallUnsupportedVersionStarted(applicationId: String) {}
    override fun onUninstallUnsupportedVersionCancelled(applicationId: String) {}
}