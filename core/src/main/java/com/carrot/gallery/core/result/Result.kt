package com.carrot.gallery.core.result

/**
 * memo. 왜 Result <> 을 covariance 로 정의했는가?
 *
 * - [Nothing]은 모든 타입의 sub class.
 * - 즉, Result<*> 로 해도 Flow 에서 Result<Nothing> (Loading or Error) 을 던질 수 있습니다.
 *
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Exception) : Result<Nothing>()

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
