package com.carrot.gallery.core.domain

import com.carrot.gallery.core.data.ImageRepository
import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.base.FlowUseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08-27
 */
class GetImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Long, Image>(dispatcher) {

    override fun execute(id: Long): Flow<Result<Image>> = flow {
        try {
            emit(Result.Loading)
            emit(Result.Success(imageRepository.getImage(id)))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
