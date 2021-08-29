package com.carrot.gallery.core.event

import androidx.lifecycle.LifecycleOwner

/**
 * Created by kyunghoon on 2021-08-29
 */
interface ViewModelSingleEventsDelegate {
    fun observeSingleEvent(lifecycleOwner: LifecycleOwner, action: (SingleEventType) -> Unit)
    fun notifySingleEvent(event: SingleEventType)
}

interface SingleEventType


/**
 * Single Event 를 SingleLiveEvent 로 구현한다.
 */
class ViewModelSingleLiveEventsDelegate : ViewModelSingleEventsDelegate {
    private val singleEvent =
        SingleLiveEvent<SingleEventType>()

    override fun observeSingleEvent(
        lifecycleOwner: LifecycleOwner,
        action: (SingleEventType) -> Unit
    ) {
        singleEvent.observe(lifecycleOwner) {
            action(it)
        }
    }

    override fun notifySingleEvent(event: SingleEventType) {
        singleEvent.value = event
    }
}