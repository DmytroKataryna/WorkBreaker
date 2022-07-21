package kataryna.app.work.breaker.data.local

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kataryna.app.work.breaker.data.sysTime.TimeRetriever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LocalPhotoStorageImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())
    private val testDataStore = PreferenceDataStoreFactory.create(
        scope = testCoroutineScope,
        produceFile =
        { testContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
    )
    private val systemTime = FakeTimeRetriever()
    private val storage = LocalPhotoStorageImpl(testDataStore, systemTime)

    @Before
    fun setUp() {
        testCoroutineScope.launch {
            systemTime.fakeSystemTime = 0
            testDataStore.edit { it.clear() }
        }
    }

    @Test
    fun savePhotoUrl() {
        testCoroutineScope.runTest {
            systemTime.fakeSystemTime = 111
            storage.savePhotoUrl("some_url")

            val data = testDataStore.data.first()
            Assert.assertEquals("some_url", data[storage.photoUrlKey])
            Assert.assertEquals(111L, data[storage.photoTimestampKey])
        }
    }

    @Test
    fun loadPhotoUrl() {
        testCoroutineScope.runTest {
            val data = testDataStore.data.first()
            Assert.assertNull(data[storage.photoUrlKey])

            testDataStore.edit { settings ->
                settings[storage.photoUrlKey] = "url"
            }

            Assert.assertEquals("url", storage.loadPhotoUrl())
        }
    }

    @Test
    fun isPhotoUrlValid() {
        testCoroutineScope.runTest {
            testDataStore.edit { settings ->
                settings[storage.photoUrlKey] = "url"
                settings[storage.photoTimestampKey] = 400L
                systemTime.fakeSystemTime = 180300
            }

            assertTrue(storage.isPhotoUrlValid())
        }
    }

    @Test
    fun isPhotoUrlValid_Null() {
        testCoroutineScope.runTest {
            assertFalse(storage.isPhotoUrlValid())
        }
    }

    @Test
    fun isPhotoUrlValid_Expired() {
        testCoroutineScope.runTest {
            testDataStore.edit { settings ->
                settings[storage.photoUrlKey] = "url"
                settings[storage.photoTimestampKey] = 400L
                systemTime.fakeSystemTime = 180500
            }

            assertFalse(storage.isPhotoUrlValid())
        }
    }

    @Test
    fun saveUserTasks() {
        testCoroutineScope.runTest {
            storage.saveUserTasks("user_task")

            val data = testDataStore.data.first()
            Assert.assertEquals("user_task", data[storage.userTasksKey])
        }
    }

    @Test
    fun getUserTasks() {
        testCoroutineScope.runTest {
            val data = testDataStore.data.first()
            Assert.assertNull(data[storage.userTasksKey])

            testDataStore.edit { settings ->
                settings[storage.userTasksKey] = "text"
            }

            Assert.assertEquals("text", storage.getUserTasks())
        }
    }

    private class FakeTimeRetriever : TimeRetriever {

        var fakeSystemTime: Long = 0L

        override fun getSystemTime(): Long {
            return fakeSystemTime
        }
    }

    companion object {
        const val TEST_DATASTORE_NAME = "test_data_file_ph"
    }
}
