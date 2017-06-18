package com.symposly.app

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Sebastian Owodzin on 07/05/2016.
 * Copyright © 2017 mobiletoolkit.org. All rights reserved.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("org.mobiletoolkit.updater.exampleapp", appContext.packageName)
    }
}