package qrsimple.franz.com.qrsimple.feature.history

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import qrsimple.franz.com.qrsimple.domain.model.QrItem
import qrsimple.franz.com.qrsimple.domain.model.QrType
import qrsimple.franz.com.qrsimple.domain.repository.QrHistoryRepository

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeQrHistoryRepository
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQrHistoryRepository()
        viewModel = HistoryViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads items from repository`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.items.size)
    }

    @Test
    fun `search query updates search in state`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.setSearchActive(true)
        viewModel.onSearchQueryChanged("github")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("github", state.searchQuery)
        assertTrue(state.isSearchActive)
        assertEquals(1, state.items.size)
        assertEquals("github.com", state.items.first().title)
    }

    @Test
    fun `toggle favorite updates item status`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val initialItem = viewModel.uiState.value.items.first()
        viewModel.toggleFavorite(initialItem.id, initialItem.isFavorite)
        advanceUntilIdle()

        assertTrue(fakeRepository.items.first { it.id == initialItem.id }.isFavorite)
    }

    @Test
    fun `filter favorites shows only favorite items`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onFilterSelected(HistoryFilter.FAVORITES)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(HistoryFilter.FAVORITES, state.selectedFilter)
        assertTrue(state.items.all { it.isFavorite })
    }

    @Test
    fun `clear all history deletes all items`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.clearAllHistory()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.items.size)
        assertFalse(state.showClearAllDialog)
    }
}

class FakeQrHistoryRepository : QrHistoryRepository {
    val items = mutableListOf(
        QrItem(id = 1, rawValue = "https://github.com", title = "github.com", type = QrType.URL, isFavorite = false),
        QrItem(id = 2, rawValue = "WIFI:S:MyWifi;P:pass;;", title = "Wi-Fi: MyWifi", type = QrType.WIFI, isFavorite = true)
    )

    private val flow = MutableStateFlow<List<QrItem>>(items.toList())

    override fun getAllHistory(): Flow<List<QrItem>> = flow
    override fun getFavorites(): Flow<List<QrItem>> = flowOf(items.filter { it.isFavorite })
    override fun search(query: String): Flow<List<QrItem>> = flowOf(
        items.filter { it.title.contains(query, ignoreCase = true) || it.rawValue.contains(query, ignoreCase = true) }
    )
    override fun getByType(type: QrType): Flow<List<QrItem>> = flowOf(items.filter { it.type == type })
    override suspend fun getById(id: Long): QrItem? = items.find { it.id == id }

    override suspend fun saveQr(rawValue: String, title: String?, metadata: String?): Long {
        val newId = (items.maxOfOrNull { it.id } ?: 0) + 1
        items.add(QrItem(id = newId, rawValue = rawValue, title = title ?: rawValue, type = QrType.TEXT))
        flow.value = items.toList()
        return newId
    }

    override suspend fun update(item: QrItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            flow.value = items.toList()
        }
    }

    override suspend fun delete(item: QrItem) {
        items.removeAll { it.id == item.id }
        flow.value = items.toList()
    }

    override suspend fun deleteById(id: Long) {
        items.removeAll { it.id == id }
        flow.value = items.toList()
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        val item = items.find { it.id == id }
        if (item != null) {
            val index = items.indexOf(item)
            items[index] = item.copy(isFavorite = isFavorite)
            flow.value = items.toList()
        }
    }

    override suspend fun updateTitle(id: Long, title: String) {
        val item = items.find { it.id == id }
        if (item != null) {
            val index = items.indexOf(item)
            items[index] = item.copy(title = title)
            flow.value = items.toList()
        }
    }

    override suspend fun clearHistory() {
        items.clear()
        flow.value = emptyList()
    }
}
