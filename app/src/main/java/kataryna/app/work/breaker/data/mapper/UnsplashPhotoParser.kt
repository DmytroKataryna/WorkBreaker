package kataryna.app.work.breaker.data.mapper

import kataryna.app.work.breaker.data.remote.dto.UnsplashRemotePhoto
import kataryna.app.work.breaker.presentation.model.UnsplashPhoto

class UnsplashPhotoParser {
    fun convert(data: UnsplashRemotePhoto): UnsplashPhoto {
        return UnsplashPhoto(data.urls?.regular.orEmpty())
    }
}
