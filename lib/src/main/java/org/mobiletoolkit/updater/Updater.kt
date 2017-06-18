package org.mobiletoolkit.updater

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import org.mobiletoolkit.updater.model.UpdatePromptData
import org.mobiletoolkit.updater.model.VersionData
import org.mobiletoolkit.updater.model.VersionsInfo

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2017 mobiletoolkit.org. All rights reserved.
 */
public class Updater(
        private val activity: Activity,
        private val applicationId: String,
        private val versionName: String,
        private val promptToRunLatestVersion: Boolean = true,
        private val promptToUninstallUnsupportedVersions: Boolean = true,
        private val callback: Callback? = null
) {
    private val TAG = "Updater"

    private val UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE = 999

    private lateinit var versionsInfo: VersionsInfo
    private lateinit var versionCheck: VersionCheck

    fun execute(versionsInfo: VersionsInfo) {
        this.versionsInfo = versionsInfo
        versionCheck = VersionCheck(applicationId, versionName, versionsInfo)

        // check if the latest app version is already installed & if it's not the current one then propose to start it instead
        if (isApplicationInstalled(activity, versionsInfo.latestVersionData) && versionsInfo.latestVersionData.applicationId != applicationId) {
            if (promptToRunLatestVersion) {
                showRunLatestVersionAlertDialog(activity)
            }
        } else {
            when (versionCheck.result) {
                VersionCheck.Result.UP_TO_DATE -> uninstallUnsupportedVersionsIfNeeded(activity)
                VersionCheck.Result.OUTDATED -> showOutdatedVersionAlertDialog(activity)
                VersionCheck.Result.UNSUPPORTED -> showUnsupportedVersionAlertDialog(activity)
            }
        }
    }

    fun isApplicationInstalled(context: Context, versionData: VersionData): Boolean {
        var result = false

        try {
            result = versionData.versionName == context.packageManager.getPackageInfo(versionData.applicationId, PackageManager.GET_META_DATA).versionName
        } catch (e: PackageManager.NameNotFoundException) {

        }

        Log.v(TAG, "isApplicationInstalled: $result [$versionData]")

        return result
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.v(TAG, "onActivityResult: user accepted the uninstall")

                    uninstallUnsupportedVersionsIfNeeded(activity)
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(TAG, "onActivityResult: user canceled the uninstall")

                    markUninstallUnsupportedVersionsDone(activity)
                }
                else -> Log.d(TAG, "onActivityResult: failed to uninstall")
            }

            return true
        }

        return false
    }

    private fun launchApplication(context: Context, applicationId: String) {
        context.startActivity(context.packageManager.getLaunchIntentForPackage(applicationId))
    }

    private fun showInGooglePlay(context: Context, applicationId: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$applicationId")))
        } catch (exception: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$applicationId")))
        }
    }

    private fun showRunLatestVersionAlertDialog(context: Context) {
        AlertDialog.Builder(context, R.style.UpdaterAlertDialogStyle)
                .setTitle(R.string.latest_version_installed_title)
                .setMessage(R.string.latest_version_installed_message)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()

                    launchApplication(context, versionsInfo.latestVersionData.applicationId)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onLatestVersionLaunchCancelled(versionCheck.result)
                }
                .show()
    }

    private fun showOutdatedVersionAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.UpdaterAlertDialogStyle)
                .setTitle(R.string.outdated_version_title)
                .setMessage(R.string.outdated_version_message)
                .setPositiveButton(R.string.update) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onVersionUpdateStarted(versionCheck.result)

                    showInGooglePlay(context, versionsInfo.latestVersionData.applicationId)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onVersionUpdateCancelled(versionCheck.result)
                }

        if (null != versionsInfo.outdatedVersionUpdatePromptData) {
            builder.setTitle((versionsInfo.outdatedVersionUpdatePromptData as UpdatePromptData).title)
            builder.setMessage((versionsInfo.outdatedVersionUpdatePromptData as UpdatePromptData).message)
        }

        builder.show()
    }

    private fun showUnsupportedVersionAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.UpdaterAlertDialogStyle)
                .setTitle(R.string.unsupported_version_title)
                .setMessage(R.string.unsupported_version_message)
                .setPositiveButton(R.string.update) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onVersionUpdateStarted(versionCheck.result)

                    showInGooglePlay(context, versionsInfo.latestVersionData.applicationId)
                }

        if (null != versionsInfo.unsupportedVersionUpdatePromptData) {
            builder.setTitle((versionsInfo.unsupportedVersionUpdatePromptData as UpdatePromptData).title)
            builder.setMessage((versionsInfo.unsupportedVersionUpdatePromptData as UpdatePromptData).message)
        }

        builder.show()
    }

    private val SHARED_PREFERENCES_FILE_NAME = "com.mobiletoolkit.updater"
    private val SHARED_PREFERENCES_KEY = "unsupported_versions_uninstall_done"

    private fun shouldUninstallUnsupportedVersions(context: Context): Boolean {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).getBoolean(SHARED_PREFERENCES_KEY, false).not() && versionsInfo.uninstallUnsupportedVersions && promptToUninstallUnsupportedVersions
    }

    private fun markUninstallUnsupportedVersionsDone(context: Context) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(SHARED_PREFERENCES_KEY, true).apply()
    }

    private fun existingUnsupportedVersions(context: Context): List<VersionData> {
        return versionsInfo.unsupportedVersions.filter { it.applicationId != applicationId && isApplicationInstalled(context, it) }
    }

    private fun uninstallUnsupportedVersionsIfNeeded(activity: Activity) {
        if (shouldUninstallUnsupportedVersions(activity)) {
            val versionsToUnistall = existingUnsupportedVersions(activity)

            if (versionsToUnistall.isEmpty()) {
                markUninstallUnsupportedVersionsDone(activity)

                callback?.onUninstallUnsupportedVersionsFinished()
            } else {
                callback?.onUninstallUnsupportedVersionsStarted()

                showUninstallUnsupportedVersionAlertDialog(activity, versionsToUnistall.first().applicationId)
            }
        } else {
            callback?.onUninstallUnsupportedVersionsSkipped()
        }
    }

    private fun showUninstallUnsupportedVersionAlertDialog(activity: Activity, applicationId: String) {
        val builder = AlertDialog.Builder(activity, R.style.UpdaterAlertDialogStyle)
                .setTitle(R.string.unsupported_version_installed_title)
                .setMessage(R.string.unsupported_version_installed_message)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()

                    uninstallApplication(activity, applicationId)

                    callback?.onUninstallUnsupportedVersionStarted(applicationId)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onUninstallUnsupportedVersionCancelled(applicationId)
                }

        if (null != versionsInfo.unsupportedVersionUpdatePromptData) {
            builder.setTitle((versionsInfo.unsupportedVersionUpdatePromptData as UpdatePromptData).title)
            builder.setMessage((versionsInfo.unsupportedVersionUpdatePromptData as UpdatePromptData).message)
        }

        builder.show()
    }

    private fun uninstallApplication(activity: Activity, applicationId: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:$applicationId"))

        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)

        activity.startActivityForResult(intent, UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE)
    }
}