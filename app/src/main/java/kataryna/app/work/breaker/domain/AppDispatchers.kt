package kataryna.app.work.breaker.domain

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {

    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher

}