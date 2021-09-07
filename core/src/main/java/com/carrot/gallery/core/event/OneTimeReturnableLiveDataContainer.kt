package com.carrot.gallery.core.event

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Created by kyunghoon on 2021-09-04
 *
 * 데이터 업데이트 이후에
 * 업데이트된 데이터를 품고 있는 LiveData 를 구독(observe)하면
 * 최초에만 LiveData(데이터)가 리턴되고,
 * 그 다음부터 observe 를 하면 dummy LiveData 가 리턴이 됩니다.
 *
 * Configuration Change 에 대응하기 위해 만들었습니다.
 */
interface OneTimeReturnableLiveDataContainer {

    /**
     * @return 1회성 notify 를 할 수 있는 LiveData 를 리턴 합니다. (configuration chanage 등에 대응할 수 있습니다.)
     */
    fun <T> getOneTimeReturnableLiveData(data: LiveData<Data<T>>, key: String): LiveData<T>

    fun <T> createDataForOneTimeReturnableLiveData(data: T, key: String): Data<T>
}

class OneTimeReturnableLiveDataContainerImple : OneTimeReturnableLiveDataContainer {

    companion object {
        const val START_VERSION = 0
    }

    private val dataVersions = ArrayMap<String, Int>()

    override fun <T> getOneTimeReturnableLiveData(data: LiveData<Data<T>>, key: String): LiveData<T> {
        data.value?.let { inputData ->
            if (inputData.version > getOneTimeReturnableLiveDataVersion(key)) {
                dataVersions[key] = inputData.version
                val liveData = MutableLiveData<T>()
                liveData.value = inputData.data
                return liveData
            }
        }
        return MutableLiveData()
    }

    override fun <T> createDataForOneTimeReturnableLiveData(data: T, key: String): Data<T> {
        return Data(data, getOneTimeReturnableLiveDataVersion(key) + 1)
    }

    private fun getOneTimeReturnableLiveDataVersion(key: String): Int {
        dataVersions[key]?.let {
            return it
        }
        dataVersions[key] = START_VERSION
        return START_VERSION
    }
}

data class Data<T>(val data: T, val version: Int)
