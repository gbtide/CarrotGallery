package com.carrot.gallery.core.domain

import com.carrot.gallery.core.data.ImageRepository
import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.base.UseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.gallery.Image
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08-27
 */
class GetImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Int, Image>(dispatcher) {
    override suspend fun execute(id: Int): Image {
        return imageRepository.getImage(id)
    }

    //    override fun execute(id: Int): Flow<Result<Image>> {
//        return flow {
//            try {
//                emit(Result.Loading)
//                val image = imageRepository.getImage(id)
//                emit(Result.Success(image))
//            } catch (e: Exception) {
//                emit(Result.Error(e))
//            }
//        }
//    }
}