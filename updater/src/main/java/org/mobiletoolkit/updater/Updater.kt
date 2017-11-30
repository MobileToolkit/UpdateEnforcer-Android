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
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
class Updater(
        private val activity: Activity,
        private val applicationId: String,
        private val versionName: String,
        private val promptToRunLatestVersion: Boolean = true,
        private val promptToUninstallUnsupportedVersions: Boolean = false,
        private val callback: Callback? = null
) {
    companion object {
        private val TAG = "Updater"

        private val UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE = 999

        private val SHARED_PREFERENCES_FILE_NAME = "com.mobiletoolkit.updater"
        private val SHARED_PREFERENCES_KEY = "unsupported_versions_uninstall_done"
    }

    private lateinit var versionsInfo: VersionsInfo
    private lateinit var versionCheckResult: VersionCheck.Result

    fun execute(versionsInfo: VersionsInfo) {
        this.versionsInfo = versionsInfo
        versionCheckResult = VersionCheck(applicationId, versionName, versionsInfo).result

        // check if the latest app version is already installed & if it's not the current one then propose to start it instead
        Log.v(TAG, "is applicationId running: ${versionsInfo.latestVersionData.applicationId == applicationId}")
        if (isPackageInstalled(activity, versionsInfo.latestVersionData) && versionsInfo.latestVersionData.applicationId != applicationId) {
            if (promptToRunLatestVersion) {
                showRunLatestVersionAlertDialog(activity)
            }
        } else {
            when (versionCheckResult) {
                VersionCheck.Result.UP_TO_DATE -> uninstallUnsupportedVersionsIfNeeded(activity)
                VersionCheck.Result.OUTDATED -> showOutdatedVersionAlertDialog(activity)
                VersionCheck.Result.UNSUPPORTED -> showUnsupportedVersionAlertDialog(activity)
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int): Boolean =
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
                    else -> {
                        Log.d(TAG, "onActivityResult: failed to uninstall")
                    }
                }
                true
            } else {
                false
            }

    private fun isPackageInstalled(context: Context, versionData: VersionData): Boolean =
            try {
                val packageInfo = context.packageManager.getPackageInfo(versionData.applicationId, PackageManager.GET_META_DATA)

                val result = packageInfo != null

                Log.v(TAG, "isPackageInstalled: $result\n" +
                        " * versionData: $versionData")

                result
            } catch (e: PackageManager.NameNotFoundException) {
                Log.v(TAG, "isPackageInstalled: false\n" +
                        "versionData: $versionData")

                false
            }

    private fun launchApplication(context: Context, applicationId: String) {
        context.startActivity(context.packageManager.getLaunchIntentForPackage(applicationId))
    }

    private fun initializeUpdate(context: Context, versionData: VersionData) {
        if (versionData.installUrl != null) {
            Log.v(TAG, "initializeUpdate -> opening url: ${versionData.installUrl}")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${versionData.installUrl}"))
            context.startActivity(intent)
        } else {
            try {
                Log.v(TAG, "initializeUpdate -> trying to open market://details?id=${versionData.applicationId}")

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${versionData.applicationId}"))
                context.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                Log.v(TAG, "initializeUpdate -> opening https://play.google.com/store/apps/details?id=${versionData.applicationId}")

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${versionData.applicationId}"))
                context.startActivity(intent)
            }
        }
    }

    private fun showRunLatestVersionAlertDialog(context: Context) {
        AlertDialog.Builder(context, R.style.MobileToolkit_Updater_Dialog_Alert)
                .setTitle(R.string.mobiletoolkit_updater_run_latest_version_title)
                .setMessage(R.string.mobiletoolkit_updater_run_latest_version_message)
                .setPositiveButton(R.string.mobiletoolkit_updater_run_latest_version_yes_button) { dialog, _ ->
                    dialog.dismiss()

                    launchApplication(context, versionsInfo.latestVersionData.applicationId)
                }
                .setNegativeButton(R.string.mobiletoolkit_updater_run_latest_version_no_button) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onLatestVersionLaunchCancelled(versionCheckResult)
                }
                .show()
    }

    private fun showOutdatedVersionAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.MobileToolkit_Updater_Dialog_Alert)
                .setTitle(R.string.mobiletoolkit_updater_outdated_version_title)
                .setMessage(R.string.mobiletoolkit_updater_outdated_version_message)
                .setPositiveButton(R.string.mobiletoolkit_updater_outdated_version_update_button) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onVersionUpdateStarted(versionCheckResult)

                    initializeUpdate(context, versionsInfo.latestVersionData)
                }
                .setNegativeButton(R.string.mobiletoolkit_updater_outdated_version_cancel_button) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onVersionUpdateCancelled(versionCheckResult)
                }

        if (null != versionsInfo.outdatedVersionUpdatePromptData) {
            builder.setTitle((versionsInfo.outdatedVersionUpdatePromptData as UpdatePromptData).title)
            builder.setMessage((versionsInfo.outdatedVersionUpdatePromptData as UpdatePromptData).message)
        }

        builder.show()
    }

    private fun showUnsupportedVersionAlertDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.MobileToolkit_Updater_Dialog_Alert)
                .setTitle(R.string.mobiletoolkit_updater_unsupported_version_title)
                .setMessage(R.string.mobiletoolkit_updater_unsupported_version_message)
                .setPositiveButton(R.string.mobiletoolkit_updater_unsupported_version_update_button) { dialog, _ ->
                    dialog.dismiss()

                    callback?.onVersionUpdateStarted(versionCheckResult)

                    initializeUpdate(context, versionsInfo.latestVersionData)
                }

        if (null != versionsInfo.unsupportedVersionUpdatePromptData) {
            builder.setTitle((versionsInfo.unsupportedVersionUpdatePromptData as UpdatePromptData).title)
            builder.setMessage((versionsInfo.unsupportedVersionUpdatePromptData as UpdatePromptData).message)
        }

        builder.show()
    }

    private fun isNotMarkedUninstallUnsupportedVersionsDone(context: Context): Boolean =
            context.getSharedPreferences(
                    SHARED_PREFERENCES_FILE_NAME,
                    Context.MODE_PRIVATE
            ).getBoolean(SHARED_PREFERENCES_KEY, false).not()

    private fun shouldUninstallUnsupportedVersions(context: Context): Boolean {
        Log.v(TAG, "shouldUninstallUnsupportedVersions\n" +
                " * promptToUninstallUnsupportedVersions: $promptToUninstallUnsupportedVersions\n" +
                " * promptToUninstallUnsupportedVersions: isNotMarkedUninstallUnsupportedVersionsDone: ${isNotMarkedUninstallUnsupportedVersionsDone(context)}\n" +
                " * shouldUninstallUnsupportedVersions -> versionsInfo.uninstallUnsupportedVersions: ${versionsInfo.uninstallUnsupportedVersions}")

        return promptToUninstallUnsupportedVersions || (isNotMarkedUninstallUnsupportedVersionsDone(context) && versionsInfo.uninstallUnsupportedVersions)
    }

    private fun markUninstallUnsupportedVersionsDone(context: Context) {
        context.getSharedPreferences(
                SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE
        ).edit().putBoolean(SHARED_PREFERENCES_KEY, true).apply()
    }

    private fun existingUnsupportedVersions(context: Context): List<VersionData> =
            versionsInfo.unsupportedVersions.filter {
                it.applicationId != applicationId && isPackageInstalled(context, it)
            }

    private fun uninstallUnsupportedVersionsIfNeeded(activity: Activity) {
        if (shouldUninstallUnsupportedVersions(activity)) {
            val versionsToUninstall = existingUnsupportedVersions(activity)
            Log.v(TAG, "uninstallUnsupportedVersionsIfNeeded\n" +
                    " * versionsToUninstall: $versionsToUninstall")

            if (versionsToUninstall.isEmpty()) {
                markUninstallUnsupportedVersionsDone(activity)

                callback?.onUninstallUnsupportedVersionsFinished()
            } else {
                callback?.onUninstallUnsupportedVersionsStarted()

                showUninstallUnsupportedVersionAlertDialog(activity, versionsToUninstall.first().applicationId)
            }
        } else {
            callback?.onUninstallUnsupportedVersionsSkipped()
        }
    }

    private fun showUninstallUnsupportedVersionAlertDialog(activity: Activity, applicationId: String) {
        val builder = AlertDialog.Builder(activity, R.style.MobileToolkit_Updater_Dialog_Alert)
                .setTitle(R.string.mobiletoolkit_updater_unsupported_versions_installed_title)
                .setMessage(R.string.mobiletoolkit_updater_unsupported_versions_installed_message)
                .setPositiveButton(R.string.mobiletoolkit_updater_ok_button) { dialog, _ ->
                    dialog.dismiss()

                    uninstallApplication(activity, applicationId)

                    callback?.onUninstallUnsupportedVersionStarted(applicationId)
                }
                .setNegativeButton(R.string.mobiletoolkit_updater_cancel_button) { dialog, _ ->
                    dialog.dismiss()

                    markUninstallUnsupportedVersionsDone(activity)

                    callback?.onUninstallUnsupportedVersionCancelled(applicationId)
                }

        builder.show()
    }

    private fun uninstallApplication(activity: Activity, applicationId: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:$applicationId"))
                .putExtra(Intent.EXTRA_RETURN_RESULT, true)

        activity.startActivityForResult(intent, UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE)
    }
}