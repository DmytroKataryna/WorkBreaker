package kataryna.app.work.breaker.helpers

import kataryna.app.work.breaker.domain.Resource
import kataryna.app.work.breaker.domain.model.UnsplashPhoto
import kataryna.app.work.breaker.domain.repo.UnsplashPhotoRepository
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
