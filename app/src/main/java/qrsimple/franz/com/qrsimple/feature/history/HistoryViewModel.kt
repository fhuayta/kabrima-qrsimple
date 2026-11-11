package qrsimple.franz.com.qrsimple.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType
import qrsimple.franz.com.qrsimple.domain.repository.QrHistoryRepository

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val qrRepository: QrHistoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchActive = MutableStateFlow(false)
    private val _selectedFilter = MutableStateFlow(HistoryFilter.ALL)
    private val _selectedItem = MutableStateFlow<QrItem?>(null)
    private val _showClearAllDialog = MutableStateFlow(false)
    private val _itemToEditTitle = MutableStateFlow<QrItem?>(null)

    private val _rawItems = combine(_searchQuery, _selectedFilter) { query, filter ->
        Pair(query.trim(), filter)
    }.flatMapLatest { (query, filter) ->
        if (query.isNotEmpty()) {
            qrRepository.search(query)
        } else {
            when (filter) {
                HistoryFilter.ALL -> qrRepository.getAllHistory()
                HistoryFilter.FAVORITES -> qrRepository.getFavorites()
                HistoryFilter.URL -> qrRepository.getByType(QrType.URL)
                HistoryFilter.WIFI -> qrRepository.getByType(QrType.WIFI)
                HistoryFilter.CONTACT -> qrRepository.getByType(QrType.CONTACT)
                HistoryFilter.TEXT -> qrRepository.getByType(QrType.TEXT)
            }
        }
    }

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = combine(
        _rawItems,
        _searchQuery,
        _isSearchActive,
        _selectedFilter
    ) { items, query, isSearchActive, filter ->
        val filteredList = if (query.isNotEmpty() && filter != HistoryFilter.ALL) {
            when (filter) {
                HistoryFilter.FAVORITES -> items.filter { it.isFavorite }
                HistoryFilter.URL -> items.filter { it.type == QrType.URL }
                HistoryFilter.WIFI -> items.filter { it.type == QrType.WIFI }
                HistoryFilter.CONTACT -> items.filter { it.type == QrType.CONTACT }
                HistoryFilter.TEXT -> items.filter { it.type == QrType.TEXT }
                else -> items
            }
        } else {
            items
        }

        _uiState.value.copy(
            items = filteredList,
            searchQuery = query,
            isSearchActive = isSearchActive,
            selectedFilter = filter,
            isLoading = false
        )
    }.combine(_selectedItem) { state, selected ->
        state.copy(selectedItem = selected)
    }.combine(_showClearAllDialog) { state, showClear ->
        state.copy(showClearAllDialog = showClear)
    }.combine(_itemToEditTitle) { state, editItem ->
        state.copy(itemToEditTitle = editItem)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun onFilterSelected(filter: HistoryFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(id: Long, currentFav: Boolean) {
        viewModelScope.launch {
            qrRepository.toggleFavorite(id, !currentFav)
            if (_selectedItem.value?.id == id) {
                _selectedItem.value = _selectedItem.value?.copy(isFavorite = !currentFav)
            }
        }
    }

    fun deleteItem(item: QrItem) {
        viewModelScope.launch {
            qrRepository.delete(item)
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = null
            }
        }
    }

    fun deleteItemById(id: Long) {
        viewModelScope.launch {
            qrRepository.deleteById(id)
            if (_selectedItem.value?.id == id) {
                _selectedItem.value = null
            }
        }
    }

    fun openClearAllDialog() {
        _showClearAllDialog.value = true
    }

    fun dismissClearAllDialog() {
        _showClearAllDialog.value = false
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            qrRepository.clearHistory()
            _showClearAllDialog.value = false
            _selectedItem.value = null
        }
    }

    fun selectItem(item: QrItem?) {
        _selectedItem.value = item
    }

    fun setItemToEditTitle(item: QrItem?) {
        _itemToEditTitle.value = item
    }

    fun saveTitle(id: Long, newTitle: String) {
        if (newTitle.isNotBlank()) {
            viewModelScope.launch {
                qrRepository.updateTitle(id, newTitle.trim())
                _itemToEditTitle.value = null
                if (_selectedItem.value?.id == id) {
                    _selectedItem.value = _selectedItem.value?.copy(title = newTitle.trim())
                }
            }
        }
    }
}
