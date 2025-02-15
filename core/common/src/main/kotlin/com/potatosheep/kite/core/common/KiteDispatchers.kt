package com.potatosheep.kite.core.common

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class Dispatcher(val kiteDispatcher: KiteDispatchers)

enum class KiteDispatchers {
    Default,
    IO
}