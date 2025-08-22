package com.potatosheep.kite.core.common

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class Service(val kiteServices: KiteServices)

enum class KiteServices {
    Download
}
