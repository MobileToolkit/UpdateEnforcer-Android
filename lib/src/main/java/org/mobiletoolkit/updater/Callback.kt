package org.mobiletoolkit.updater

/**
 * Created by Sebastian Owodzin on 17/06/2017.
 */
public interface Callback {
    fun onLatestVersionLaunchCancelled(versionCheckResult: VersionCheck.Result)

    fun onVersionUpdateStarted(versionCheckResult: VersionCheck.Result)
    fun onVersionUpdateCancelled(versionCheckResult: VersionCheck.Result)

    fun onUninstallUnsupportedVersionsStarted()
    fun onUninstallUnsupportedVersionsSkipped()
    fun onUninstallUnsupportedVersionsFinished()

    fun onUninstallUnsupportedVersionStarted(applicationId: String)
    fun onUninstallUnsupportedVersionCancelled(applicationId: String)
}