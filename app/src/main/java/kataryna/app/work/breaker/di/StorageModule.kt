package kataryna.app.work.breaker.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kataryna.app.work.breaker.data.local.LocalLocationStorage
import kataryna.app.work.breaker.data.local.LocalLocationStorageImpl
import kataryna.app.work.breaker.data.local.LocalPhotoStorage
import kataryna.app.work.breaker.data.local.LocalPhotoStorageImpl
import kataryna.app.work.breaker.data.sysTime.TimeRetriever
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideLocalPhotoStorage(
        @ApplicationContext appContext: Context,
        timeRetriever: TimeRetriever
    ): LocalPhotoStorage {
        val settingsDataStore = PreferenceDataStoreFactory.create {
            appContext.preferencesDataStoreFile("settings")
        }
        return LocalPhotoStorageImpl(settingsDataStore, timeRetriever)
    }

    @Provides
    @Singleton
    fun provideLocationStorage(
        @ApplicationContext appContext: Context
    ): LocalLocationStorage {
        val locationDataStore = PreferenceDataStoreFactory.create {
            appContext.preferencesDataStoreFile("location")
        }
        return LocalLocationStorageImpl(locationDataStore)
    }
}
