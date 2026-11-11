package qrsimple.franz.com.qrsimple.feature.history

import qrsimple.franz.com.qrsimple.R

enum class HistoryFilter(val labelRes: Int, val emoji: String) {
    ALL(R.string.history_filter_all, ""),
    FAVORITES(R.string.history_filter_favorites, "⭐"),
    URL(R.string.history_filter_urls, "🌐"),
    WIFI(R.string.history_filter_wifi, "📶"),
    CONTACT(R.string.history_filter_contacts, "👤"),
    TEXT(R.string.history_filter_text, "📄")
}
