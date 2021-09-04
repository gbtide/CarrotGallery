package com.carrot.gallery.core.util

import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by kyunghoon on 2021-08
 */
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun <T> PublishSubject<T>.observeByDebounce(timeoutMilliseconds: Long, consumer: Consumer<T>): Disposable {
    return this
        .debounce(timeoutMilliseconds, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { value ->
            consumer.accept(value)
        }
}