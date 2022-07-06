package kataryna.app.work.breaker.data.local

interface LocalPhotoStorage {
    suspend fun photoUrlValid(): Boolean
    suspend fun loadPhotoUrl(): String?
    suspend fun savePhotoUrl(url: String)
    suspend fun saveUserTasks(text: String)
    suspend fun getUserTasks(): String?
}