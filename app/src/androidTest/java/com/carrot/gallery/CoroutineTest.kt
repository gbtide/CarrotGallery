package com.carrot.gallery

import android.telephony.TelephonyManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.Test

/**
 * Created by kyunghoon on 2022-03-09
 */
class CoroutineTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun test() {
        val telephonyManager : TelephonyManager? = null
        telephonyManager?.let {
            it.deviceId
            it.meid
            it.imei
        }
    }
}