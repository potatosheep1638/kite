package com.potatosheep.kite.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import com.potatosheep.kite.core.designsystem.vectors.ArrowDropUp
import com.potatosheep.kite.core.designsystem.vectors.BaselineWifiTethering
import com.potatosheep.kite.core.designsystem.vectors.Bolt
import com.potatosheep.kite.core.designsystem.vectors.Bookmark
import com.potatosheep.kite.core.designsystem.vectors.ChatBubble
import com.potatosheep.kite.core.designsystem.vectors.FileDownload
import com.potatosheep.kite.core.designsystem.vectors.Link
import com.potatosheep.kite.core.designsystem.vectors.OutlineBook
import com.potatosheep.kite.core.designsystem.vectors.Sort
import com.potatosheep.kite.core.designsystem.vectors.UnfoldMore

object KiteIcons {
    // Navigation bar
    val Home = Icons.Outlined.Home
    val HomeSelected = Icons.Rounded.Home
    val Subscription = Icons.Outlined.Notifications
    val SubscriptionSelected = Icons.Rounded.Notifications
    val Library = Icons.Outlined.Bookmark
    val LibrarySelected = Icons.Rounded.Bookmark

    // Post cards
    val Image = R.drawable.image
    val Upvotes = Icons.Outlined.KeyboardArrowUp
    val ChatBubble = Icons.Outlined.ChatBubble
    val Link = Icons.Rounded.Link
    val ExitToApp = Icons.AutoMirrored.Outlined.ExitToApp
    val Play = Icons.Rounded.PlayArrow

    // Comment cards
    val MoreReplies = Icons.AutoMirrored.Rounded.ArrowForward
    val Expand = Icons.Rounded.UnfoldMore

    // Subreddit top bar
    val Subscribers = Icons.Outlined.Person
    val ActiveUsers = Icons.Rounded.BaselineWifiTethering

    // Subreddit screen
    val About = Icons.Outlined.Info
    val Wiki = Icons.Outlined.OutlineBook

    // User screen
    val Karma = Icons.Rounded.Bolt

    // Search screen
    val Add = Icons.Rounded.Add

    // Common
    val Back = Icons.AutoMirrored.Rounded.ArrowBack
    val Sort = Icons.Rounded.Sort
    val MoreOptions = Icons.Rounded.MoreVert
    val DropdownAlt = Icons.Rounded.ArrowDropDown
    val Collapse = Icons.Rounded.ArrowDropUp
    val Search = Icons.Rounded.Search
    val Check = Icons.Rounded.Check
    val Clear = Icons.Rounded.Clear
    val Share = Icons.Rounded.Share
    val Download = Icons.Rounded.FileDownload
    val Bookmark = Icons.Outlined.Bookmark
    val Bookmarked = Icons.Rounded.Bookmark
}