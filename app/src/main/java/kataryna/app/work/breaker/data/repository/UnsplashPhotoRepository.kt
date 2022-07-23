package kataryna.app.work.breaker.data.repository

import kataryna.app.work.breaker.data.Resource
import kataryna.app.work.breaker.presentation.model.UnsplashPhoto
import kotlinx.coroutines.flow.Flow

interface UnsplashPhotoRepository {

    suspend fun getBackgroundPhoto(): Flow<Resource<UnsplashPhoto>>

    suspend fun saveUserTasks(text: String)

    suspend fun fetchUserTasks(): Flow<Resource<String?>>
}
