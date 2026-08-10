package com.potatosheep.kite.core.network.client

import okhttp3.Headers
import javax.inject.Singleton

@Singleton
class CloudflareHeaders(var headers: Headers)