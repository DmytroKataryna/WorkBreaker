package kataryna.app.work.breaker.data.repository

import kataryna.app.work.breaker.data.local.LocalPhotoStorage
import kataryna.app.work.breaker.data.mapper.UnsplashPhotoParser
import kataryna.app.work.breaker.data.remote.UnsplashAPI
import kataryna.app.work.breaker.domain.repo.UnsplashPhotoRepository
import kataryna.app.work.breaker.domain.model.UnsplashPhoto
import kataryna.app.work.breaker.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoRepositoryImpl @Inject constructor(
    private val api: UnsplashAPI,
    private val local: LocalPhotoStorage,
    private val parser: UnsplashPhotoParser
) : UnsplashPhotoRepository {

    override suspend fun getBackgroundPhoto(): Flow<Resource<UnsplashPhoto>> {
        return flow {
            emit(Resource.Loading())
            if (local.isPhotoUrlValid()) {
                emit(Resource.Success(data = UnsplashPhoto(local.loadPhotoUrl().orEmpty())))
            } else {
                val result = loadFromRemote()
                emit(result)
            }
        }
    }

    private suspend fun loadFromRemote(): Resource<UnsplashPhoto> {
        return try {
            val data = api.getRandomPhoto()
            val result = parser.convert(data)
            local.savePhotoUrl(result.url)
            Resource.Success(data = result)
        } catch (e: IOException) {
            Timber.e(e)
            Resource.Error(message = "Couldn't load random photo")
        } catch (e: HttpException) {
            Timber.w("Load photo HttpException: " + e.response())
            Resource.Error(message = "Couldn't load random photo")
        }
    }

    override suspend fun saveUserTasks(text: String) = local.saveUserTasks(text)

    override suspend fun fetchUserTasks(): Flow<Resource<String?>> {
        return flow {
            emit(Resource.Loading())
            emit(Resource.Success(data = local.getUserTasks()))
        }
    }
}