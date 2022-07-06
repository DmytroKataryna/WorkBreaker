package kataryna.app.work.breaker.data.sysTime

class TimeRetrieverImpl : TimeRetriever {

    override fun getSystemTime() = System.currentTimeMillis()
}