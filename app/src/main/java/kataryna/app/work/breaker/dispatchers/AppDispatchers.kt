package kataryna.app.work.breaker.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {

    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val default: CoroutineDispatcher
}
