package kataryna.app.work.breaker.data.remote

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

    //TODO hide keys
    companion object {
        const val API_KEY = "Y9wpetECD7luCQFARXc7rPvTa8AL4gFjUG79161G31w"
        const val SECRET_KEY = "FXIJAEDqI_aH5jMAufpONE0prYyBvArxw-jG0wlnqPU"
        const val BASE_URL = "https://api.unsplash.com/"
        const val TOPIC_ID = "iUIsnVtjB0Y"
    }

}