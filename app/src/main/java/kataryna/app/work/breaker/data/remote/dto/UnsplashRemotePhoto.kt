package kataryna.app.work.breaker.data.remote.dto

import com.squareup.moshi.Json

data class UnsplashRemotePhoto(
    @field:Json(name = "color") var color: String?,
    var alt_description: String?,
    var created_at: String?,
    var description: String?,
    var downloads: Int?,
    var exif: Exif? = Exif(),
    var height: Int?,
    var id: String?,
    var likes: Int?,
    var promoted_at: String?,
    var updated_at: String?,
    var urls: Urls? = Urls(),
    var views: Int?,
    var width: Int?
)
