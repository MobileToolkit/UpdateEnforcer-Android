package org.mobiletoolkit.updateenforcer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.mobiletoolkit.updateenforcer.model.Version;
import org.mobiletoolkit.updateenforcer.model.VersionInfo;

import java.util.List;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class Updater {

    private static String LOG_TAG = Updater.class.getSimpleName();

    private static Integer UNINSTALL_REQUEST_CODE = 999;

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

    public void setVersionInfo(@NonNull VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    public void setListener(@NonNull Listener listener) {
        this.listener = listener;
    }

    @NonNull
    public VersionCheck.Result run() {
        versionCheck = new VersionCheck(appVersionName, versionInfo);

        // TODO: check if the latest version is already installed & alert - do you want to launch latest app version?

        switch (versionCheck.getResult()) {
            case UNSUPPORTED:
                showUnsupportedVersionAlert();
                break;
            case OUTDATED:
                showOutdatedVersionAlert();
                break;
            case UP_TO_DATE:
                uninstallUnsupportedVersions();
                break;
        }

        return versionCheck.getResult();
    }

    @NonNull
    public Boolean handleOnActivityResult(@NonNull Integer requestCode, @NonNull Integer resultCode, @NonNull Intent data) {
        if (requestCode.equals(UNINSTALL_REQUEST_CODE)) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(LOG_TAG, "onActivityResult: user accepted the (un)install");
                    uninstallUnsupportedVersions();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(LOG_TAG, "onActivityResult: user canceled the (un)install");
                    break;
                case Activity.RESULT_FIRST_USER:
                    Log.d(LOG_TAG, "onActivityResult: failed to (un)install");
                    break;
            }

            return true;
        }

        return false;
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

    private void showLatestVersionInGooglePlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW) {{
            setData(Uri.parse(String.format("market://details?id=%s", versionCheck.getLatestVersionApplicationId())));
        }};

        activity.startActivity(intent);
    }

    private void uninstallUnsupportedVersions() {
        // TODO: alert - do you want to uninstall unsupported app versions?

        for (Version version : versionInfo.getUnsupportedVersions()) {
            String applicationId = version.getApplicationId();
            if (null != applicationId && !applicationId.equals(appApplicationId) && isApplicationInstalled(applicationId)) {
                unistallApplication(applicationId);
            }
        }
    }

    @NonNull
    private Boolean isApplicationInstalled(@NonNull String applicationId) {
        try {
            activity.getPackageManager().getPackageInfo(applicationId, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        return true;
    }

    private void unistallApplication(String applicationId) {
//        activity.startActivity(new Intent(Intent.ACTION_UNINSTALL_PACKAGE).setData(Uri.parse("package:" + applicationId)));

        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + applicationId));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        activity.startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
    }

    public interface Listener {
        void unsupportedVersionUpdateStarted();

        void outdatedVersionUpdateStarted();
        void outdatedVersionUpdateCancelled();
    }

}
