package kataryna.app.work.breaker.data.local

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LocalLocationStorageImplTest {

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
    private val storage = LocalLocationStorageImpl(testDataStore)

    @Before
    fun setUp() {
        testCoroutineScope.launch {
            testDataStore.edit { it.clear() }
        }
    }

    @Test
    fun saveLocation() {
        testCoroutineScope.runTest {
            val location = LatLng(32.0, 11.0)
            storage.saveLocation(location)

            val data = testDataStore.data.first()
            assertEquals(11.0, data[storage.longitudeKey])
            assertEquals(32.0, data[storage.latitudeKey])
        }
    }

    @Test
    fun getGeoLocation() {
        testCoroutineScope.runTest {
            val loc1 = storage.getGeoLocation()
            assertNull(loc1)

            val location = LatLng(33.0, 12.0)
            storage.saveLocation(location)

            val loc2 = storage.getGeoLocation()
            assertEquals(12.0, loc2!!.longitude, 0.1)
            assertEquals(33.0, loc2.latitude, 0.1)
        }
    }

    @Test
    fun clearLocation() {
        testCoroutineScope.runTest {
            val location = LatLng(33.0, 12.0)
            storage.saveLocation(location)
            assertNotNull(storage.getGeoLocation())

            storage.clearLocation()
            assertNull(storage.getGeoLocation())
        }
    }

    @Test
    fun isLocationValid() {
        testCoroutineScope.runTest {
            val location = LatLng(33.0, 12.0)
            storage.saveLocation(location)
            assertTrue(storage.isLocationValid())

            val location2 =
                LatLng(LocalLocationStorageImpl.INVALID, LocalLocationStorageImpl.INVALID)
            storage.saveLocation(location2)
            assertFalse(storage.isLocationValid())
        }
    }

    companion object {
        const val TEST_DATASTORE_NAME = "test_data_file"
    }
}