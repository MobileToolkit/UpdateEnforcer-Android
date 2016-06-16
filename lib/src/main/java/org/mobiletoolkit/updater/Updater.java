package org.mobiletoolkit.updater;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.mobiletoolkit.updater.model.Version;
import org.mobiletoolkit.updater.model.VersionInfo;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class Updater {

    private static String LOG_TAG = Updater.class.getSimpleName();

    private static Integer UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE = 999;

    private Activity activity;
    private String appApplicationId;
    private String appVersionName;

    private VersionInfo versionInfo;

    private Listener listener;

    private VersionCheck versionCheck;

    public Updater(@NonNull Activity activity, @NonNull String appVersionName, @NonNull String appApplicationId) {
        this.activity = activity;
        this.appApplicationId = appApplicationId;
        this.appVersionName = appVersionName;
    }

    public void setListener(@NonNull Listener listener) {
        this.listener = listener;
    }

    @NonNull
    public VersionCheck.Result run(@NonNull final VersionInfo versionInfo) {
        this.versionInfo = versionInfo;

        versionCheck = new VersionCheck(appVersionName, appApplicationId, versionInfo);

        // check if the latest app version is already installed & propose to start it instead
        if (isApplicationVersionInstalled(versionInfo.getLatestVersion().getApplicationId(), versionInfo.getLatestVersion().getVersionName()) && !appApplicationId.equals(versionInfo.getLatestVersion().getApplicationId())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(R.string.latest_version_installed_title)
                    .setMessage(R.string.latest_version_installed_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            launchApplication(versionInfo.getLatestVersion().getApplicationId());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            if (null != listener) {
                                listener.latestVersionLaunchCancelled();
                            }
                        }
                    });

            builder.create().show();
        } else {
            switch (versionCheck.getResult()) {
                case UP_TO_DATE:
                    uninstallUnsupportedVersionsIfNeeded();
                    break;
                case OUTDATED:
                    showOutdatedVersionAlert();
                    break;
                case UNSUPPORTED:
                    showUnsupportedVersionAlert();
                    break;
            }
        }

        return versionCheck.getResult();
    }

    @NonNull
    public Boolean handleOnActivityResult(@NonNull Integer requestCode, @NonNull Integer resultCode, @NonNull Intent data) {
        if (requestCode.equals(UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE)) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(LOG_TAG, "handleOnActivityResult: user accepted the (un)install");
                    performNextUnsupportedVersionUninstall();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(LOG_TAG, "handleOnActivityResult: user canceled the (un)install");
                    break;
                case Activity.RESULT_FIRST_USER:
                    Log.d(LOG_TAG, "handleOnActivityResult: failed to (un)install");
                    break;
            }

            return true;
        }

        return false;
    }

    private void showOutdatedVersionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.outdated_version_title)
                .setMessage(R.string.outdated_version_message)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (null != listener) {
                            listener.outdatedVersionUpdateStarted();
                        }

                        showLatestVersionInGooglePlay();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (null != listener) {
                            listener.outdatedVersionUpdateCancelled();
                        }
                    }
                });

        if (null != versionInfo.getOutdatedVersionUpdatePrompt()) {
            if (null != versionInfo.getOutdatedVersionUpdatePrompt().getTitle()) {
                builder.setTitle(versionInfo.getOutdatedVersionUpdatePrompt().getTitle());
            }

            if (null != versionInfo.getOutdatedVersionUpdatePrompt().getMessage()) {
                builder.setMessage(versionInfo.getOutdatedVersionUpdatePrompt().getMessage());
            }
        }

        builder.create().show();
    }

    private void showUnsupportedVersionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.unsupported_version_title)
                .setMessage(R.string.unsupported_version_message)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (null != listener) {
                            listener.unsupportedVersionUpdateStarted();
                        }

                        showLatestVersionInGooglePlay();
                    }
                });

        if (null != versionInfo.getUnsupportedVersionUpdatePrompt()) {
            if (null != versionInfo.getUnsupportedVersionUpdatePrompt().getTitle()) {
                builder.setTitle(versionInfo.getUnsupportedVersionUpdatePrompt().getTitle());
            }

            if (null != versionInfo.getUnsupportedVersionUpdatePrompt().getMessage()) {
                builder.setMessage(versionInfo.getUnsupportedVersionUpdatePrompt().getMessage());
            }
        }

        builder.create().show();
    }

    private void showLatestVersionInGooglePlay() {
        final String applicationId = versionInfo.getLatestVersion().getApplicationId();

        Intent intent = new Intent(Intent.ACTION_VIEW) {{
            setData(Uri.parse(String.format("market://details?id=%s", applicationId)));
        }};

        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", applicationId))));
        }
    }

    private void uninstallUnsupportedVersionsIfNeeded() {
        if (versionInfo.getUninstallUnsupportedVersions()) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("com.mobiletoolkit.updater", Context.MODE_PRIVATE);

            Boolean unsupportedVersionsUninstallDone = sharedPreferences.getBoolean("unsupported_versions_uninstall_done", false);

            Boolean unsupportedVersionsInstalled = false;
            for (Version version : versionInfo.getUnsupportedVersions()) {
                if (!appApplicationId.equals(version.getApplicationId()) && isApplicationVersionInstalled(version.getApplicationId(), version.getVersionName())) {
                    unsupportedVersionsInstalled = true;
                    break;
                }
            }

            if (unsupportedVersionsInstalled && !unsupportedVersionsUninstallDone) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setTitle(R.string.unsupported_version_installed_title)
                        .setMessage(R.string.unsupported_version_installed_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                if (null != listener) {
                                    listener.uninstallUnsupportedVersionsStarted();
                                }

                                performNextUnsupportedVersionUninstall();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                if (null != listener) {
                                    listener.uninstallUnsupportedVersionsCancelled();
                                }
                            }
                        });

                builder.create().show();
            } else {
                if (null != listener) {
                    listener.uninstallUnsupportedVersionsSkipped();
                }
            }

            sharedPreferences.edit().putBoolean("unsupported_versions_uninstall_done", true).apply();
        } else {
            if (null != listener) {
                listener.uninstallUnsupportedVersionsSkipped();
            }
        }
    }

    private void performNextUnsupportedVersionUninstall() {
        for (Version version : versionInfo.getUnsupportedVersions()) {
            String appId = version.getApplicationId();
            if (null != appId && !appId.equals(appApplicationId) && isApplicationVersionInstalled(appId, version.getVersionName())) {
                uninstallApplication(appId);
                return;
            }
        }

        if (null != listener) {
            listener.uninstallUnsupportedVersionsFinished();
        }
    }

    @NonNull
    private Boolean isApplicationVersionInstalled(@NonNull String applicationId, @NonNull String versionName) {
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(applicationId, PackageManager.GET_META_DATA);

            return versionName.equals(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void launchApplication(@NonNull String applicationId) {
        activity.startActivity(activity.getPackageManager().getLaunchIntentForPackage(applicationId));
    }

    private void uninstallApplication(String applicationId) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + applicationId));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        activity.startActivityForResult(intent, UNSUPPORTED_VERSION_UNINSTALL_REQUEST_CODE);
    }

    public interface Listener {
        void latestVersionLaunchCancelled();

        void outdatedVersionUpdateStarted();
        void outdatedVersionUpdateCancelled();

        void unsupportedVersionUpdateStarted();

        void uninstallUnsupportedVersionsStarted();
        void uninstallUnsupportedVersionsSkipped();
        void uninstallUnsupportedVersionsFinished();
        void uninstallUnsupportedVersionsCancelled();
    }

}
