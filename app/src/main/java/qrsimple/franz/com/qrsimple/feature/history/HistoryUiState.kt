package qrsimple.franz.com.qrsimple.feature.history

import qrsimple.franz.com.qrsimple.domain.model.QrItem

data class HistoryUiState(
    val items: List<QrItem> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val selectedItem: QrItem? = null,
    val showClearAllDialog: Boolean = false,
    val itemToEditTitle: QrItem? = null,
    val isLoading: Boolean = false
)

