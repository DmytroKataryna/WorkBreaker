package kataryna.app.work.breaker.helpers

import kataryna.app.work.breaker.data.Resource
import kataryna.app.work.breaker.presentation.model.UnsplashPhoto
import kataryna.app.work.breaker.data.repository.UnsplashPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUnsplashPhotoRepository : UnsplashPhotoRepository {

    override suspend fun getBackgroundPhoto(): Flow<Resource<UnsplashPhoto>> {
        return flow { }
    }

    override suspend fun saveUserTasks(text: String) {
    }

    override suspend fun fetchUserTasks(): Flow<Resource<String?>> {
        return flow { }
    }
}
