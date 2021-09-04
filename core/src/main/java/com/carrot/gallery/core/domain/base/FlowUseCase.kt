package com.carrot.gallery.core.domain.base

import com.carrot.gallery.core.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * memo.
     *
     * 1.
     * "flowOn" example:
     *  withContext(Dispatchers.Main) {
     *      val singleValue = intFlow // will be executed on IO if context wasn't specified before
     *              .map { ... } // Will be executed in IO
     *              .flowOn(Dispatchers.IO)
     *              .filter { ... } // Will be executed in Default
     *              .flowOn(Dispatchers.Default)
     *              .single() // Will be executed in the Main
     *  }
     *
     *  2.
     *  flow는 cold stream. collect 전까지 수행되지 않는다.
     *
     *  3.
     *  flow {} 내부는 suspend 가능
     */
    operator fun invoke(parameter: P): Flow<Result<R>> = execute(parameter)
        .catch { e -> emit(Result.Error(Exception(e))) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameter: P): Flow<Result<R>>

}
