package kataryna.app.work.breaker.data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kataryna.app.work.breaker.data.local.LocalPhotoStorage
import kataryna.app.work.breaker.data.mapper.UnsplashPhotoParser
import kataryna.app.work.breaker.data.remote.UnsplashAPI
import kataryna.app.work.breaker.data.remote.dto.UnsplashRemotePhoto
import kataryna.app.work.breaker.data.remote.dto.Urls
import kataryna.app.work.breaker.domain.model.UnsplashPhoto
import kataryna.app.work.breaker.domain.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import retrofit2.HttpException

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class UnsplashPhotoRepositoryImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun getBackgroundPhoto() {
        runBlocking {
            val local: LocalPhotoStorage = mock {
                onBlocking { isPhotoUrlValid() } doReturn true
                onBlocking { loadPhotoUrl() } doReturn "photo_url"
            }
            assertRepo(storage = local) {
                val result = getBackgroundPhoto().toList()
                verify(local).isPhotoUrlValid()
                verify(local).loadPhotoUrl()

                Assert.assertEquals(2, result.size)
                Assert.assertTrue(result[0] is Resource.Loading)
                Assert.assertTrue(result[1] is Resource.Success)
                Assert.assertEquals("photo_url", (result[1] as Resource.Success).data!!.url)
            }
        }
    }

    @Test
    fun getBackgroundPhoto_RemoteLoad() {
        runBlocking {
            val local: LocalPhotoStorage = mock {
                onBlocking { isPhotoUrlValid() } doReturn false
                onBlocking { savePhotoUrl(any()) } doReturn Unit
            }
            val remote: UnsplashAPI = mock {
                onBlocking { getRandomPhoto(any(), any()) } doReturn (UnsplashRemotePhoto(
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
                ))
            }
            val parser: UnsplashPhotoParser = mock {
                onBlocking { convert(any()) } doReturn (UnsplashPhoto("photo_url"))
            }
            assertRepo(unsplash = remote, storage = local, parserPhoto = parser) {
                val result = getBackgroundPhoto().toList()
                verify(local).isPhotoUrlValid()
                verify(remote).getRandomPhoto(any(), any())
                verify(parser).convert(any())
                verify(local).savePhotoUrl(eq("photo_url"))

                Assert.assertEquals(2, result.size)
                Assert.assertTrue(result[0] is Resource.Loading)
                Assert.assertTrue(result[1] is Resource.Success)
                Assert.assertEquals("photo_url", (result[1] as Resource.Success).data!!.url)
            }
        }
    }

    @Test
    fun getBackgroundPhoto_RemoteError() {
        runBlocking {
            val local: LocalPhotoStorage = mock {
                onBlocking { isPhotoUrlValid() } doReturn false
            }
            val remote: UnsplashAPI = mock {
                onBlocking { getRandomPhoto(any(), any()) } doThrow (HttpException::class)
            }
            val parser: UnsplashPhotoParser = mock()
            assertRepo(unsplash = remote, storage = local, parserPhoto = parser) {
                val result = getBackgroundPhoto().toList()
                verify(local).isPhotoUrlValid()
                verify(remote).getRandomPhoto(any(), any())
                verify(parser, never()).convert(any())
                verify(local, never()).savePhotoUrl(any())

                Assert.assertEquals(2, result.size)
                Assert.assertTrue(result[0] is Resource.Loading)
                Assert.assertTrue(result[1] is Resource.Error)
            }
        }
    }

    @Test
    fun saveUserTasks() {
        runBlocking {
            val local: LocalPhotoStorage = mock()
            assertRepo(storage = local) {
                saveUserTasks("text")
                verify(local).saveUserTasks(eq("text"))
            }
        }
    }

    @Test
    fun getGeoLocation() {
        runBlocking {
            val local: LocalPhotoStorage = mock()
            assertRepo(storage = local) {
                val result = fetchUserTasks().toList()
                verify(local).getUserTasks()
                Assert.assertEquals(2, result.size)
                Assert.assertTrue(result[0] is Resource.Loading)
                Assert.assertTrue(result[1] is Resource.Success)
            }
        }
    }

    private suspend fun assertRepo(
        unsplash: UnsplashAPI = mock(),
        storage: LocalPhotoStorage = mock(),
        parserPhoto: UnsplashPhotoParser = mock(),
        action: suspend UnsplashPhotoRepositoryImpl.() -> Unit
    ) {
        action.invoke(UnsplashPhotoRepositoryImpl(unsplash, storage, parserPhoto))
    }
}