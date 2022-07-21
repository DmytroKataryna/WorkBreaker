package kataryna.app.work.breaker.data.mapper

import junit.framework.Assert.assertEquals
import kataryna.app.work.breaker.data.remote.dto.UnsplashRemotePhoto
import kataryna.app.work.breaker.data.remote.dto.Urls
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UnsplashPhotoParserTest {

    private val parser = UnsplashPhotoParser()

    @Test
    fun convertRemoteData() {
        val data = UnsplashRemotePhoto(
            color = "Color",
            alt_description = "Alt description",
            created_at = "Created at",
            description = "Description",
            downloads = 33,
            height = 55,
            id = "ID",
            likes = 111,
            promoted_at = "Promoted at",
            updated_at = "Updated at",
            views = 4,
            width = 5,
            urls = Urls(regular = "photo url")
        )

        val result = parser.convert(data)
        assertEquals(result.url, "photo url")
    }

    @Test
    fun convertRemoteDataNullUrl() {
        val data = UnsplashRemotePhoto(
            color = "Color",
            alt_description = "Alt description",
            created_at = "Created at",
            description = "Description",
            downloads = 33,
            height = 55,
            id = "ID",
            likes = 111,
            promoted_at = "Promoted at",
            updated_at = "Updated at",
            views = 4,
            width = 5,
            urls = null
        )

        val result = parser.convert(data)
        assertEquals(result.url, "")
    }
}
