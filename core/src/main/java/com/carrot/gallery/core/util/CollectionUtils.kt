package com.carrot.gallery.core.util

/**
 * Created by kyunghoon on 2021-08-28
 */
object CollectionUtils {

    fun isEmpty(collection: Collection<Any>?): Boolean {
        return collection == null || collection.isEmpty()
    }
}
