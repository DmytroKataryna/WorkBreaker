package kataryna.app.work.breaker.data.remote.dto

data class Exif(
    var aperture: String? = null,
    var exposure_time: String? = null,
    var focal_length: String? = null,
    var iso: Int? = null,
    var make: String? = null,
    var model: String? = null,
    var name: String? = null
)