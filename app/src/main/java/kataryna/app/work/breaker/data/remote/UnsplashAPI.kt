package kataryna.app.work.breaker.data.remote

import kataryna.app.work.breaker.BuildConfig
import kataryna.app.work.breaker.data.remote.dto.UnsplashRemotePhoto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashAPI {

    @Headers("Accept-Version: v1")
    @GET("/photos/random")
    suspend fun getRandomPhoto(
        @Query("client_id") key: String = API_KEY,
        @Query("topics") topics: String = TOPIC_ID
    ): UnsplashRemotePhoto

    companion object {
        const val API_KEY = BuildConfig.UNSPLASH_API_KEY
        const val BASE_URL = "https://api.unsplash.com/"
        const val TOPIC_ID = "iUIsnVtjB0Y"
    }
}
