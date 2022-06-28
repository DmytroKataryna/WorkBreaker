package kataryna.app.work.breaker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kataryna.app.work.breaker.data.repository.UnsplashPhotoRepositoryImpl
import kataryna.app.work.breaker.domain.UnsplashPhotoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUnsplashRepository(
        stockRepositoryImpl: UnsplashPhotoRepositoryImpl
    ): UnsplashPhotoRepository

}