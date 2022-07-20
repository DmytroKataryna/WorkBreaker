package kataryna.app.work.breaker.domain.repo

import kataryna.app.work.breaker.domain.model.UnsplashPhoto
import kataryna.app.work.breaker.domain.Resource
import kotlinx.coroutines.flow.Flow

interface UnsplashPhotoRepository {

    suspend fun getBackgroundPhoto(): Flow<Resource<UnsplashPhoto>>

    suspend fun saveUserTasks(text: String)

    suspend fun fetchUserTasks(): Flow<Resource<String?>>

}