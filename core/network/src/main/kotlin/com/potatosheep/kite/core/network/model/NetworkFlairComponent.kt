package com.potatosheep.kite.core.network.model

import com.potatosheep.kite.core.model.FlairComponent
import com.potatosheep.kite.core.model.FlairComponentType

data class NetworkFlairComponent(
    val type: String,
    val value: String
)

internal fun NetworkFlairComponent.toExternalModel() = FlairComponent(
    value = this.value,
    type = if (type == FlairComponentType.TEXT.value) {
        FlairComponentType.TEXT
    } else {
        FlairComponentType.EMOJI
    }
)
