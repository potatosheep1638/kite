package com.potatosheep.kite.core.common.enums

import com.potatosheep.kite.core.translation.R.string as Translation

object SortOption {

    enum class Post(val label: Int, val uri: String) {
        HOT(
            label = Translation.hot,
            uri = "hot"
        ),
        NEW(
            label = Translation.sort_new,
            uri = "new"
        ),
        TOP(
            label = Translation.top,
            uri = "top"
        ),
        RISING(
            label = Translation.rising,
            uri = "rising"
        ),
        CONTROVERSIAL(
            label = Translation.controversial,
            uri = "controversial"
        )
    }

    enum class Comment(val label: Int, val uri: String) {
        CONFIDENCE(
            label = Translation.confidence,
            uri = "confidence"
        ),
        TOP(
            label = Translation.top,
            uri = "top"
        ),
        NEW(
            label = Translation.sort_new,
            uri = "new"
        ),
        CONTROVERSIAL(
            label = Translation.controversial,
            uri = "controversial"
        ),
        OLD(
            label = Translation.old,
            uri = "old"
        )
    }


    enum class User(val label: Int, val uri: String) {
        HOT(
            label = Translation.hot,
            uri = "hot"
        ),
        NEW(
            label = Translation.sort_new,
            uri = "new"
        ),
        TOP(
            label = Translation.top,
            uri = "top"
        ),
        CONTROVERSIAL(
            label = Translation.controversial,
            uri = "controversial"
        )
    }

    enum class Search(val label: Int, val uri: String) {
        RELEVANCE(
            label = Translation.relevance,
            uri = "relevance"
        ),
        HOT(
            label = Translation.hot,
            uri = "hot"
        ),
        TOP(
            label = Translation.top,
            uri = "top"
        ),
        NEW(
            label = Translation.sort_new,
            uri = "new"
        ),
        COMMENTS(
            label = Translation.comments,
            uri = "comments"
        )
    }

    enum class Timeframe(val label: Int, val uri: String) {
        HOUR(
            label = Translation.hour,
            uri = "hour"
        ),
        DAY(
            label = Translation.day,
            uri = "day"
        ),
        WEEK(
            label = Translation.week,
            uri = "week"
        ),
        MONTH(
            label = Translation.month,
            uri = "month"
        ),
        YEAR(
            label = Translation.year,
            uri = "year"
        ),
        ALL(
            label = Translation.all,
            uri = "all"
        ),
    }
}