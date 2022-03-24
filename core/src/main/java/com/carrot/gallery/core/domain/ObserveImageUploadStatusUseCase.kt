package com.carrot.gallery.core.domain

import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.base.FlowUseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.ImageUploadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kyunghoon on 2022-02-23
 */
class ObserveImageUploadStatusUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, ImageUploadState>(dispatcher) {

    override fun execute(parameter: Unit): Flow<Result<ImageUploadState>> = flow {
        emit(Result.Success(ImageUploadState.Complete("")))
    }
}