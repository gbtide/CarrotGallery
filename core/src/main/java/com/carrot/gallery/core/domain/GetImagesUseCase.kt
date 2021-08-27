package com.carrot.gallery.core.domain

import com.carrot.gallery.core.data.ImageRepository
import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.base.FlowUseCase
import com.carrot.gallery.core.domain.base.UseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.gallery.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08-27
 */
class GetImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<GetImageParameter, List<Image>>(dispatcher) {

    override suspend fun execute(param: GetImageParameter): List<Image> {
        return imageRepository.getImages(param.page, param.limit)
    }

    //    override fun execute(param: GetImageParameter): List<Image> {
//        return flow {
//            try {
//                emit(Result.Loading)
//                emit(Result.Success(imageRepository.getImages()))
//            } catch (e: Exception) {
//                emit(Result.Error(e))
//            }
//        }
//    }
}

data class GetImageParameter(
    val page: Int,
    val limit: Int,
)
