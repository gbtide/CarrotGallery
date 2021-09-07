package com.carrot.gallery.core.domain.base

import com.carrot.gallery.core.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    operator fun invoke(parameter: P): Flow<Result<R>> = execute(parameter)
        .catch { e -> emit(Result.Error(Exception(e))) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameter: P): Flow<Result<R>>
}
