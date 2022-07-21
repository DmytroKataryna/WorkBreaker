package kataryna.app.work.breaker.extensions

fun Double?.orZero(): Double {
    return this ?: 0.0
}

fun Int?.orZero(): Int {
    return this ?: 0
}
