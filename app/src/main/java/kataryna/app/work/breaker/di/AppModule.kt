package kataryna.app.work.breaker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kataryna.app.work.breaker.data.mapper.UnsplashPhotoParser
import kataryna.app.work.breaker.data.remote.UnsplashAPI
import kataryna.app.work.breaker.data.sysTime.TimeRetriever
import kataryna.app.work.breaker.data.sysTime.TimeRetrieverImpl
import kataryna.app.work.breaker.dispatchers.AppDispatcherImpl
import kataryna.app.work.breaker.dispatchers.AppDispatchers
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
    fun provideUnsplashApi(): UnsplashAPI {
        return Retrofit.Builder()
            .baseUrl(UnsplashAPI.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                ).build()
            ).build()
            .create()
    }

    @Provides
    @Singleton
    fun provideDispatchers(): AppDispatchers {
        return AppDispatcherImpl()
    }

    @Provides
    @Singleton
    fun provideTimeRetriever(): TimeRetriever {
        return TimeRetrieverImpl()
    }

    @Provides
    @Singleton
    fun provideUnsplashPhotoParser(): UnsplashPhotoParser {
        return UnsplashPhotoParser()
    }
}
