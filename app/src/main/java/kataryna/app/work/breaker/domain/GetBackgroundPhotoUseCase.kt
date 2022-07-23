package kataryna.app.work.breaker.domain

import kataryna.app.work.breaker.data.Resource
import kataryna.app.work.breaker.data.repository.UnsplashPhotoRepository
import kataryna.app.work.breaker.presentation.model.UnsplashPhoto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

//In such small proj it is redundant to create domain layer with such simple UseCases
//Created just for demo sake

@Suppress("unused")
class GetBackgroundPhotoUseCase(
    private val repository: UnsplashPhotoRepository,
    private val dispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): Flow<Resource<UnsplashPhoto>> {
        return withContext(dispatcher) {
            repository.getBackgroundPhoto()
        }
    }
}
