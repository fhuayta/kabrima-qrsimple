package qrsimple.franz.com.qrsimple.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import qrsimple.franz.com.qrsimple.R
import qrsimple.franz.com.qrsimple.core.theme.DarkBackground
import qrsimple.franz.com.qrsimple.core.theme.DarkSurface
import qrsimple.franz.com.qrsimple.core.theme.EmeraldPrimary
import qrsimple.franz.com.qrsimple.feature.history.components.ClearHistoryDialog
import qrsimple.franz.com.qrsimple.feature.history.components.EditTitleDialog
import qrsimple.franz.com.qrsimple.feature.history.components.HistoryFilterChips
import qrsimple.franz.com.qrsimple.feature.history.components.HistoryItemRow
import qrsimple.franz.com.qrsimple.feature.history.components.HistorySearchBar
import qrsimple.franz.com.qrsimple.feature.result.QrResultBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onOpenSupport: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            if (uiState.isSearchActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 6.dp)
                ) {
                    HistorySearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                        onClose = { viewModel.setSearchActive(false) }
                    )
                }
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(R.string.history_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (uiState.items.isNotEmpty()) {
                                Text(
                                    text = stringResource(R.string.history_count, uiState.items.size),
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.setSearchActive(true) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.history_search_action_desc),
                                tint = Color.White
                            )
                        }
                        if (uiState.items.isNotEmpty()) {
                            IconButton(onClick = { viewModel.openClearAllDialog() }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = stringResource(R.string.history_clear_action_desc),
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        IconButton(onClick = onOpenAbout) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.about_action_desc),
                                tint = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        IconButton(onClick = onOpenSupport) {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = stringResource(R.string.scanner_support_desc),
                                tint = EmeraldPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground
                    )
                )
            }
        },
        containerColor = DarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Chips Row
            HistoryFilterChips(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { viewModel.onFilterSelected(it) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Body content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = EmeraldPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                uiState.items.isEmpty() -> {
                    // Empty State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DarkSurface,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (uiState.searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.History,
                                        contentDescription = null,
                                        tint = EmeraldPrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) {
                                    stringResource(R.string.history_search_empty_title)
                                } else {
                                    stringResource(R.string.history_empty_title)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) {
                                    stringResource(R.string.history_search_empty_subtitle)
                                } else {
                                    stringResource(R.string.history_empty_subtitle)
                                },
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                else -> {
                    // List of items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.items,
                            key = { it.id }
                        ) { item ->
                            HistoryItemRow(
                                item = item,
                                onClick = { viewModel.selectItem(item) },
                                onToggleFavorite = { viewModel.toggleFavorite(item.id, item.isFavorite) },
                                onDelete = { viewModel.deleteItem(item) },
                                onEditTitle = { viewModel.setItemToEditTitle(item) }
                            )
                        }
                    }
                }
            }
        }

        // Clear All Dialog
        if (uiState.showClearAllDialog) {
            ClearHistoryDialog(
                onConfirm = { viewModel.clearAllHistory() },
                onDismiss = { viewModel.dismissClearAllDialog() }
            )
        }

        // Edit Title Dialog
        uiState.itemToEditTitle?.let { item ->
            EditTitleDialog(
                item = item,
                onSave = { newTitle -> viewModel.saveTitle(item.id, newTitle) },
                onDismiss = { viewModel.setItemToEditTitle(null) }
            )
        }

        // Result Bottom Sheet when tapping an item
        uiState.selectedItem?.let { item ->
            QrResultBottomSheet(
                item = item,
                onDismiss = { viewModel.selectItem(null) },
                onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) }
            )
        }
    }
}

