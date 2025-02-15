package com.potatosheep.kite.core.common.enums

import com.potatosheep.kite.core.common.R

object SortOption {

    enum class Post(val label: Int, val uri: String) {
        HOT(
            label = R.string.hot,
            uri = "hot"
        ),
        NEW(
            label = R.string.sort_new,
            uri = "new"
        ),
        TOP(
            label = R.string.top,
            uri = "top"
        ),
        RISING(
            label = R.string.rising,
            uri = "rising"
        ),
        CONTROVERSIAL(
            label = R.string.controversial,
            uri = "controversial"
        )
    }

    enum class Comment(val label: Int, val uri: String) {
        CONFIDENCE(
            label = R.string.confidence,
            uri = "confidence"
        ),
        TOP(
            label = R.string.top,
            uri = "top"
        ),
        NEW(
            label = R.string.sort_new,
            uri = "new"
        ),
        CONTROVERSIAL(
            label = R.string.controversial,
            uri = "controversial"
        ),
        OLD(
            label = R.string.old,
            uri = "old"
        )
    }


    enum class User(val label: Int, val uri: String) {
        HOT(
            label = R.string.hot,
            uri = "hot"
        ),
        NEW(
            label = R.string.sort_new,
            uri = "new"
        ),
        TOP(
            label = R.string.top,
            uri = "top"
        ),
        CONTROVERSIAL(
            label = R.string.controversial,
            uri = "controversial"
        )
    }

    enum class Search(val label: Int, val uri: String) {
        RELEVANCE(
            label = R.string.relevance,
            uri = "relevance"
        ),
        HOT(
            label = R.string.hot,
            uri = "hot"
        ),
        TOP(
            label = R.string.top,
            uri = "top"
        ),
        NEW(
            label = R.string.sort_new,
            uri = "new"
        ),
        COMMENTS(
            label = R.string.comments,
            uri = "comments"
        )
    }

    enum class Timeframe(val label: Int, val uri: String) {
        HOUR(
            label = R.string.hour,
            uri = "hour"
        ),
        DAY(
            label = R.string.day,
            uri = "day"
        ),
        WEEK(
            label = R.string.week,
            uri = "week"
        ),
        MONTH(
            label = R.string.month,
            uri = "month"
        ),
        YEAR(
            label = R.string.year,
            uri = "year"
        ),
        ALL(
            label = R.string.all,
            uri = "all"
        ),
    }
}