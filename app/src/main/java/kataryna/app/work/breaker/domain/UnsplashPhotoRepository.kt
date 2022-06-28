package kataryna.app.work.breaker.domain

import kataryna.app.work.breaker.domain.model.UnsplashPhoto
import kataryna.app.work.breaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UnsplashPhotoRepository {

    suspend fun getBackgroundPhoto(): Flow<Resource<UnsplashPhoto>>

    suspend fun saveUserTasks(text: String)

    suspend fun fetchUserTasks(): String?

}