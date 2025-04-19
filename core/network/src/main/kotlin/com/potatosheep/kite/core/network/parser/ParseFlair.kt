package com.potatosheep.kite.core.network.parser

import com.potatosheep.kite.core.model.FlairComponentType
import com.potatosheep.kite.core.network.model.NetworkFlairComponent
import org.jsoup.nodes.Element

/**
 * Returns a post flair as a [NetworkFlairComponent] object. Do note that if there are multiple
 * elements with the '.post_flair' class, only the first element will be used.
 */
internal fun extractPostFlair(element: Element, instanceUrl: String): List<NetworkFlairComponent> {
    val flairComponent = mutableListOf<NetworkFlairComponent>()

    element.select("a.post_flair").first()?.forEachNode { node ->
        if (node is Element) {
            if (node.hasClass(FlairComponentType.EMOJI.value)) {
                flairComponent.add(
                    NetworkFlairComponent(
                        type = FlairComponentType.EMOJI.value,
                        value = "${instanceUrl}${node.attr("style").split("'")[1]}"
                    )
                )
            } else if (node.`is`("span")) {
                flairComponent.add(
                    NetworkFlairComponent(
                        type = FlairComponentType.TEXT.value,
                        value = node.text()
                    )
                )
            }
        }
    }

    return flairComponent
}

internal fun extractPostFlairId(element: Element): String {
    return element.select("a.post_flair")
        .first()
        ?.attr("href")
        ?.split("%3A%22", "%22&")
        ?.get(1) ?: ""
}

/**
 * Returns a comment flair as a [NetworkFlairComponent] object. Do note that if there are multiple
 * elements with the '.post_flair' class, only the first element will be used.
 */
internal fun extractCommentFlair(element: Element, instanceUrl: String): List<NetworkFlairComponent> {
    val flairComponent = mutableListOf<NetworkFlairComponent>()

    element.select("small.author_flair").first()?.forEachNode { node ->
        if (node is Element) {
            if (node.hasClass(FlairComponentType.EMOJI.value)) {
                flairComponent.add(
                    NetworkFlairComponent(
                        type = FlairComponentType.EMOJI.value,
                        value = "${instanceUrl}${node.attr("style").split("'")[1]}"
                    )
                )
            } else if (node.`is`("span")) {
                flairComponent.add(
                    NetworkFlairComponent(
                        type = FlairComponentType.TEXT.value,
                        value = node.text()
                    )
                )
            }
        }
    }

    return flairComponent
}