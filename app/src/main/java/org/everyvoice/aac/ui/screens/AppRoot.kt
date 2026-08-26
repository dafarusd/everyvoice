package org.everyvoice.aac.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.everyvoice.aac.data.ButtonEntity
import org.everyvoice.aac.speech.VoiceState
import org.everyvoice.aac.ui.AacViewModel
import org.everyvoice.aac.ui.Notice
import org.everyvoice.aac.ui.Screen
import org.everyvoice.aac.ui.components.SentenceStripBar
import org.everyvoice.aac.ui.components.VoiceWarning
import org.everyvoice.aac.ui.components.TileButton

/**
 * The whole app on one scaffold: top bar, sentence strip pinned to the
 * bottom, and either the category grid, a category's tiles, or search
 * results in the middle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(viewModel: AacViewModel, voiceState: VoiceState) {
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val tiles by viewModel.tiles.collectAsStateWithLifecycle()
    val stripTiles by viewModel.stripTiles.collectAsStateWithLifecycle()
    val editMode by viewModel.editMode.collectAsStateWithLifecycle()
    val columns by viewModel.columns.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var showEditGate by remember { mutableStateOf(false) }
    var editingTile by remember { mutableStateOf<ButtonEntity?>(null) }
    var addingToCategory by remember { mutableStateOf<String?>(null) }
    var menuOpen by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri -> uri?.let(viewModel::exportBackup) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::importBackup) }

    LaunchedEffect(Unit) {
        viewModel.notices.collect { notice ->
            val text = when (notice) {
                is Notice.Info -> notice.text
                is Notice.Error -> notice.text
            }
            snackbarHostState.showSnackbar(text)
        }
    }

    BackHandler(enabled = screen is Screen.Category) {
        viewModel.goHome()
    }

    val title = when (val s = screen) {
        is Screen.Home -> "EveryVoice"
        is Screen.Category -> categories.firstOrNull { it.id == s.categoryId }?.name ?: "EveryVoice"
    }

    Scaffold(
        topBar = {
            // Both live in the top slot so TopAppBar keeps applying the
            // status bar inset for the whole group; a banner placed above the
            // Scaffold would draw under the status bar instead.
            Column {
                TopAppBar(
                title = { Text(title, fontSize = 22.sp) },
                navigationIcon = {
                    if (screen is Screen.Category) {
                        TextButton(onClick = viewModel::goHome) {
                            Text("← Home", fontSize = 17.sp)
                        }
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val sizes = listOf(2, 3, 4, 6)
                        val next = sizes[(sizes.indexOf(columns) + 1) % sizes.size]
                        viewModel.setColumns(next)
                    }) {
                        Text("${columns}×", fontSize = 16.sp)
                    }
                    IconButton(onClick = {
                        if (editMode) viewModel.setEditMode(false) else showEditGate = true
                    }) {
                        Text(if (editMode) "🔓" else "🔒", fontSize = 20.sp)
                    }
                    Box {
                        IconButton(onClick = { menuOpen = true }) {
                            Text("⋮", fontSize = 22.sp)
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Back up vocabulary") },
                                onClick = {
                                    menuOpen = false
                                    exportLauncher.launch("everyvoice-backup.zip")
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Restore from backup") },
                                onClick = {
                                    menuOpen = false
                                    importLauncher.launch(
                                        arrayOf("application/zip", "application/octet-stream")
                                    )
                                },
                            )
                        }
                    }
                },
                )
                VoiceWarning(voiceState)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SentenceStripBar(
                tiles = stripTiles,
                onSpeak = viewModel::speakStrip,
                onBackspace = viewModel::stripBackspace,
                onClear = viewModel::stripClear,
            )
        },
    ) { padding ->
        when (val s = screen) {
            is Screen.Home -> HomeContent(
                padding = padding,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                searchResults = searchResults,
                onSearchResultTapped = viewModel::onSearchResultTapped,
                categories = categories.map { it.id to Triple(it.name, it.icon, 0) },
                onCategoryTapped = viewModel::openCategory,
            )

            is Screen.Category -> TileGrid(
                padding = padding,
                tiles = tiles,
                columns = columns,
                editMode = editMode,
                onTileTapped = { button ->
                    if (editMode) editingTile = button else viewModel.onTileTapped(button)
                },
                onAddTapped = { addingToCategory = s.categoryId },
            )
        }
    }

    if (showEditGate) {
        AlertDialog(
            onDismissRequest = { showEditGate = false },
            title = { Text("Caregiver edit mode") },
            text = {
                Text(
                    "Edit mode is for caregivers and therapists. It lets you add, " +
                        "change, and delete buttons. Turn it on?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setEditMode(true)
                    showEditGate = false
                }) { Text("Turn on") }
            },
            dismissButton = {
                TextButton(onClick = { showEditGate = false }) { Text("Cancel") }
            },
        )
    }

    editingTile?.let { tile ->
        TileEditDialog(
            title = "Edit button",
            initialLabel = tile.label,
            initialSpeakText = tile.speakText,
            initialIcon = tile.icon,
            confirmLabel = "Save",
            onConfirm = { label, speak, icon, _ ->
                viewModel.updateTile(tile, label, speak, icon)
                editingTile = null
            },
            onDelete = {
                viewModel.deleteTile(tile)
                editingTile = null
            },
            onDismiss = { editingTile = null },
        )
    }

    addingToCategory?.let { categoryId ->
        TileEditDialog(
            title = "Add a new button",
            initialLabel = "",
            initialSpeakText = "",
            initialIcon = "",
            confirmLabel = "Add",
            onConfirm = { label, speak, icon, photo ->
                viewModel.addTile(categoryId, label, speak, icon, photo)
                addingToCategory = null
            },
            onDismiss = { addingToCategory = null },
            allowPhoto = true,
        )
    }
}

@Composable
private fun HomeContent(
    padding: PaddingValues,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<org.everyvoice.aac.engine.Tile>,
    onSearchResultTapped: (org.everyvoice.aac.engine.Tile) -> Unit,
    categories: List<Pair<String, Triple<String, String, Int>>>,
    onCategoryTapped: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Type to find a word…") },
            singleLine = true,
        )

        if (searchQuery.isNotBlank()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(searchResults, key = { it.stableKey }) { tile ->
                    TileButton(
                        label = tile.label,
                        icon = "",
                        imagePath = null,
                        onClick = { onSearchResultTapped(tile) },
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(categories, key = { it.first }) { (id, info) ->
                    val (name, icon, _) = info
                    Card(
                        onClick = { onCategoryTapped(id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.4f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(icon, fontSize = 40.sp)
                            Text(
                                name,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TileGrid(
    padding: PaddingValues,
    tiles: List<ButtonEntity>,
    columns: Int,
    editMode: Boolean,
    onTileTapped: (ButtonEntity) -> Unit,
    onAddTapped: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(tiles, key = { it.id }) { button ->
            TileButton(
                label = button.label,
                icon = button.icon,
                imagePath = button.imagePath,
                onClick = { onTileTapped(button) },
            )
        }
        if (editMode) {
            item(key = "add") {
                Card(
                    onClick = onAddTapped,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("＋", fontSize = 36.sp)
                        Text("Add", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
