package kataryna.app.work.breaker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kataryna.app.work.breaker.data.local.LocalLocationStorage
import kataryna.app.work.breaker.data.local.LocalLocationStorageImpl
import kataryna.app.work.breaker.data.local.LocalPhotoStorage
import kataryna.app.work.breaker.data.local.LocalPhotoStorageImpl
import kataryna.app.work.breaker.data.mapper.UnsplashPhotoParser
import kataryna.app.work.breaker.data.remote.UnsplashAPI
import kataryna.app.work.breaker.data.sysTime.TimeRetriever
import kataryna.app.work.breaker.data.sysTime.TimeRetrieverImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimeRetriever(): TimeRetriever {
        return TimeRetrieverImpl()
    }

    @Provides
    @Singleton
    fun provideLocalPhotoStorage(
        @ApplicationContext appContext: Context,
        timeRetriever: TimeRetriever,
    ): LocalPhotoStorage {
        return LocalPhotoStorageImpl(appContext, timeRetriever)
    }

    @Provides
    @Singleton
    fun provideLocationStorage(
        @ApplicationContext appContext: Context
    ): LocalLocationStorage {
        return LocalLocationStorageImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideUnsplashPhotoParser(): UnsplashPhotoParser {
        return UnsplashPhotoParser()
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(): UnsplashAPI {
        return Retrofit.Builder()
            .baseUrl(UnsplashAPI.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }).build()
            ).build()
            .create()
    }
}