package org.mobiletoolkit.updateenforcer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import org.mobiletoolkit.updateenforcer.model.Version;
import org.mobiletoolkit.updateenforcer.model.VersionInfo;

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
public class Enforcer {

    private Activity activity;
    private String appVersionName;
    private String appApplicationId;
    private VersionInfo versionInfo;

    private VersionChecker versionChecker;

    public Enforcer(@NonNull Activity activity,
                    @NonNull String appVersionName,
                    @NonNull String appApplicationId,
                    @NonNull VersionInfo versionInfo) {
        this.activity = activity;
        this.appVersionName = appVersionName;
        this.appApplicationId = appApplicationId;
        this.versionInfo = versionInfo;
    }

    public void run() {
        versionChecker = new VersionChecker(activity, appVersionName, versionInfo);

        switch (versionChecker.getResult()) {
            case UNSUPPORTED:
                showUnsupportedAlert();
                break;
            case OUTDATED:
                showOutdatedAlert();
                break;
            case UP_TO_DATE:
                uninstallUnsupportedVersions();
                break;
        }
    }

    private void showUnsupportedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.unsupported_version_title)
                .setMessage(R.string.unsupported_version_message)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        openInGooglePlay();
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

    private void showOutdatedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.outdated_version_title)
                .setMessage(R.string.outdated_version_message)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        openInGooglePlay();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
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

    private void openInGooglePlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW) {{
            setData(Uri.parse(String.format("market://details?id=%s", versionChecker.getLatestVersionApplicationId())));
        }};

        activity.startActivity(intent);
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
        activity.startActivity(new Intent(Intent.ACTION_UNINSTALL_PACKAGE).setData(Uri.parse("package:" + applicationId)));
    }

    private void uninstallUnsupportedVersions() {
        for (Version version : versionInfo.getUnsupportedVersions()) {
            String applicationId = version.getApplicationId();
            if (null != applicationId && !applicationId.equals(appApplicationId) && isApplicationInstalled(applicationId)) {
                unistallApplication(applicationId);
            }
        }
    }

}
