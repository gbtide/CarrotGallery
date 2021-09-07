package com.carrot.gallery.core.di

import com.carrot.gallery.core.event.OneTimeReturnableLiveDataContainer
import com.carrot.gallery.core.event.OneTimeReturnableLiveDataContainerImple
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.event.ViewModelSingleLiveEventsDelegate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by kyunghoon on 2021-08-29
 */
@InstallIn(ViewModelComponent::class)
@Module
class ViewModelScopedModule {

    @ViewModelScoped
    @Provides
    fun provideViewModelSingleEventsDelegate(): ViewModelSingleEventsDelegate = ViewModelSingleLiveEventsDelegate()

    @ViewModelScoped
    @Provides
    fun provideOneTimeReturnableLiveDataContainer(): OneTimeReturnableLiveDataContainer = OneTimeReturnableLiveDataContainerImple()
}
