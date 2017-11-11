package org.mobiletoolkit.updater

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mobiletoolkit.updater.model.VersionsInfo
import java.io.InputStreamReader

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2016 mobiletoolkit.org. All rights reserved.
 */
@RunWith(Parameterized::class)
class VersionCheckUnitTest(val packageName: String, val versionName: String, val result: VersionCheck.Result) {
    val versionInfoData = Gson().fromJson(InputStreamReader(javaClass.getResourceAsStream("version_info.json")), VersionsInfo::class.java)

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Array<Any>> {
            val packageName = "org.mobiletoolkit.updater.exampleapp"
            val packageNameOld = "org.mobiletoolkit.updater.exampleapp.old"

            return listOf(
                    arrayOf(packageName, "3.0", VersionCheck.Result.UP_TO_DATE),
                    arrayOf(packageName, "2.0.1", VersionCheck.Result.UP_TO_DATE),
                    arrayOf(packageName, "2.0", VersionCheck.Result.UP_TO_DATE),
                    arrayOf(packageName, "1.6", VersionCheck.Result.OUTDATED),
                    arrayOf(packageName, "1.5", VersionCheck.Result.UNSUPPORTED),
                    arrayOf(packageName, "0.9", VersionCheck.Result.UNSUPPORTED),
                    arrayOf(packageName, "0.4.5", VersionCheck.Result.UNSUPPORTED),
                    arrayOf(packageName, "0.3", VersionCheck.Result.UNSUPPORTED),
                    arrayOf(packageName, "0.2", VersionCheck.Result.UNSUPPORTED),

                    arrayOf(packageNameOld, "3.0", VersionCheck.Result.OUTDATED),
                    arrayOf(packageNameOld, "2.0.1", VersionCheck.Result.OUTDATED),
                    arrayOf(packageNameOld, "2.0", VersionCheck.Result.OUTDATED),
                    arrayOf(packageNameOld, "1.6", VersionCheck.Result.OUTDATED),
                    arrayOf(packageNameOld, "1.5", VersionCheck.Result.OUTDATED),
                    arrayOf(packageNameOld, "0.9", VersionCheck.Result.OUTDATED),
                    arrayOf(packageNameOld, "0.4.5", VersionCheck.Result.UNSUPPORTED),
                    arrayOf(packageNameOld, "0.3", VersionCheck.Result.UNSUPPORTED),
                    arrayOf(packageNameOld, "0.2", VersionCheck.Result.UNSUPPORTED)
            )
        }
    }

    @Test
    fun verifyVersionCheckResult() {
        assertEquals(VersionCheck(packageName, versionName, versionInfoData).result, result)
    }
}