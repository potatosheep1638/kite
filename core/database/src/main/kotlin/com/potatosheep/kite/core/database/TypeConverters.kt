package com.potatosheep.kite.core.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.potatosheep.kite.core.model.FlairComponent
import com.potatosheep.kite.core.model.MediaLink
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import kotlinx.datetime.Instant
import javax.inject.Inject

@ProvidedTypeConverter
internal class MediaLinksConverter @Inject constructor(
    moshi: dagger.Lazy<Moshi>
) {
    private val type: ParameterizedType = Types.newParameterizedType(List::class.java, MediaLink::class.java)
    private val jsonAdapter: JsonAdapter<List<MediaLink>> = moshi.get().adapter(type)

    @TypeConverter
    fun jsonToList(string: String?): List<MediaLink>? {
        return string?.let(jsonAdapter::fromJson)
    }

    @TypeConverter
    fun listToJson(list: List<MediaLink>?): String? {
        return jsonAdapter.toJson(list)
    }
}

@ProvidedTypeConverter
internal class FlairComponentsConverter @Inject constructor(
    moshi: Moshi
) {
    private val type: ParameterizedType = Types.newParameterizedType(List::class.java, FlairComponent::class.java)
    private val jsonAdapter: JsonAdapter<List<FlairComponent>> = moshi.adapter(type)

    @TypeConverter
    fun jsonToList(string: String?): List<FlairComponent>? {
        return string?.let(jsonAdapter::fromJson)
    }

    @TypeConverter
    fun listToJson(list: List<FlairComponent>?): String? {
        return jsonAdapter.toJson(list)
    }
}

internal class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}