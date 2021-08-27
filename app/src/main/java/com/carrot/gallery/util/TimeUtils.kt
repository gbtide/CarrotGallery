package com.carrot.gallery.util

import java.util.concurrent.TimeUnit

/**
 * Created by kyunghoon on 2021-01-10
 */
class TimeUtils {

    companion object {
        fun parseToHHmmss(milliseconds: Long): String {
            return if (milliseconds > 60 * 60 * 1000) {
                String.format(
                    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
                )
            } else {
                String.format(
                    "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
                )
            }
        }
    }
}