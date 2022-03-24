package com.carrot.gallery.core.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Created by kyunghoon on 2022-02-23
 */
class TickHandler(
    private val externalScope: CoroutineScope,
    private val tickInterval: Long = 5000
) {
    // Flow << SharedFlow << StateFlow
    private val _tickFlow = MutableSharedFlow<Unit>()
    val tickFlow: SharedFlow<Unit> = _tickFlow

    init {
        externalScope.launch {
            while (true) {
                _tickFlow.tryEmit(Unit)
                delay(tickInterval)
            }
        }
    }
}