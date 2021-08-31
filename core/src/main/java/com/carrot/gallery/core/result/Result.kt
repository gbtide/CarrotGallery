package com.carrot.gallery.core.result

import java.lang.Exception

/**
 * memo. 왜 Result <> 을 covariance 로 정의했는가?
 *
 * - [Nothing]은 모든 타입의 sub class.
 * - 즉, Result<*> 로 해도 Flow 에서 Result<Nothing> (Loading or Error) 을 던질 수 있습니다.
 *
 */
sealed class Result<out R> {
    companion object {
        fun createEmptySuccess() = Success(Any())
    }

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Throwable) : Result<Nothing>()

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

//val Result<*>.succeededButDataNull
//    get() = this is Result.Success && this.data == null
//
//val Result<*>.dataStatus: Result<*>
//    get() = run {
//        return if (this.succeededButDataNull) {
//            Result.Error(NullDataInSuccessResultException("Result is Success but data is null"))
//        } else {
//            this
//        }
//    }
//
//class NullDataInSuccessResultException(message: String): Exception(message)
