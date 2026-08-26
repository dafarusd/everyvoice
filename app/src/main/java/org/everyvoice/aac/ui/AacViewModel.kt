package org.everyvoice.aac.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.everyvoice.aac.backup.BackupManager
import org.everyvoice.aac.data.AacDatabase
import org.everyvoice.aac.data.ButtonEntity
import org.everyvoice.aac.data.CategoryEntity
import org.everyvoice.aac.data.ImageStore
import org.everyvoice.aac.data.Repository
import org.everyvoice.aac.data.toTile
import org.everyvoice.aac.engine.Search
import org.everyvoice.aac.engine.SentenceStrip
import org.everyvoice.aac.engine.Tile

/** What the user is looking at. */
sealed interface Screen {
    data object Home : Screen
    data class Category(val categoryId: String) : Screen
}

/** One-line results surfaced to the UI as a snackbar-style message. */
sealed interface Notice {
    data class Info(val text: String) : Notice
    data class Error(val text: String) : Notice
}

@OptIn(ExperimentalCoroutinesApi::class)
class AacViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AacDatabase.get(app)
    private val repo = Repository(db)
    private val images = ImageStore(app)
    private val backup = BackupManager(app)

    private val strip = SentenceStrip()

    // --- Screen navigation -------------------------------------------------

    private val _screen = MutableStateFlow<Screen>(Screen.Home)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    // --- Data flows ----------------------------------------------------------

    val categories: StateFlow<List<CategoryEntity>> =
        repo.observeCategories()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val tiles: StateFlow<List<ButtonEntity>> =
        _screen.flatMapLatest { s ->
            when (s) {
                is Screen.Home -> flowOf(emptyList())
                is Screen.Category -> repo.observeButtons(s.categoryId)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Every tile in every category, for search. Loaded once, refreshed lazily. */
    private val _allTiles = MutableStateFlow<List<Tile>>(emptyList())

    // --- Sentence strip -------------------------------------------------------

    private val _stripTiles = MutableStateFlow<List<Tile>>(emptyList())
    val stripTiles: StateFlow<List<Tile>> = _stripTiles.asStateFlow()

    // --- Edit mode --------------------------------------------------------------

    private val _editMode = MutableStateFlow(false)
    val editMode: StateFlow<Boolean> = _editMode.asStateFlow()

    // --- Grid size ---------------------------------------------------------------

    private val _columns = MutableStateFlow(DEFAULT_COLUMNS)
    val columns: StateFlow<Int> = _columns.asStateFlow()

    // --- Search --------------------------------------------------------------------

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Tile>>(emptyList())
    val searchResults: StateFlow<List<Tile>> = _searchResults.asStateFlow()

    // --- Events out to the UI ---------------------------------------------------------

    private val _speakRequests = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val speakRequests: SharedFlow<String> = _speakRequests.asSharedFlow()

    private val _notices = MutableSharedFlow<Notice>(extraBufferCapacity = 4)
    val notices: SharedFlow<Notice> = _notices.asSharedFlow()

    init {
        viewModelScope.launch {
            repo.ensureSeeded()
            refreshAllTiles()
        }
    }

    private suspend fun refreshAllTiles() {
        _allTiles.value = repo.exportData().second.map { it.toTile() }
    }

    // --- Navigation ------------------------------------------------------------

    fun openCategory(categoryId: String) {
        _screen.value = Screen.Category(categoryId)
    }

    fun goHome() {
        _screen.value = Screen.Home
    }

    // --- Tile taps --------------------------------------------------------------

    /** A tap adds to the strip and speaks the tile's word immediately. */
    fun onTileTapped(button: ButtonEntity) {
        if (_editMode.value) return // in edit mode taps open the editor instead
        strip.add(button.toTile())
        _stripTiles.value = strip.contents
        _speakRequests.tryEmit(button.speakText)
    }

    fun onSearchResultTapped(tile: Tile) {
        strip.add(tile)
        _stripTiles.value = strip.contents
        _speakRequests.tryEmit(tile.speakText)
    }

    fun speakStrip() {
        val text = strip.text()
        if (text.isNotEmpty()) _speakRequests.tryEmit(text)
    }

    fun stripBackspace() {
        strip.removeLast()
        _stripTiles.value = strip.contents
    }

    fun stripClear() {
        strip.clear()
        _stripTiles.value = strip.contents
    }

    // --- Edit mode --------------------------------------------------------------

    fun setEditMode(on: Boolean) {
        _editMode.value = on
    }

    fun setColumns(count: Int) {
        _columns.value = count.coerceIn(MIN_COLUMNS, MAX_COLUMNS)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = Search.query(_allTiles.value, query)
    }

    fun addTile(
        categoryId: String,
        label: String,
        speakText: String,
        icon: String,
        photo: Bitmap?,
    ) {
        if (label.isBlank()) return
        viewModelScope.launch {
            val imagePath = photo?.let { images.save(it) }
            repo.addButton(categoryId, label, speakText, imagePath, icon)
            refreshAllTiles()
            _notices.emit(Notice.Info("Added \"$label\""))
        }
    }

    fun updateTile(button: ButtonEntity, label: String, speakText: String, icon: String) {
        if (label.isBlank()) return
        viewModelScope.launch {
            repo.updateButton(
                button.copy(
                    label = label.trim(),
                    speakText = speakText.trim().ifEmpty { label.trim() },
                    icon = icon,
                )
            )
            refreshAllTiles()
            _notices.emit(Notice.Info("Saved \"$label\""))
        }
    }

    fun deleteTile(button: ButtonEntity) {
        viewModelScope.launch {
            images.delete(button.imagePath)
            repo.deleteButton(button)
            refreshAllTiles()
            _notices.emit(Notice.Info("Deleted \"${button.label}\""))
        }
    }

    // --- Backup / restore ------------------------------------------------------------

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            runCatching { backup.export(uri, repo.exportData()) }
                .onSuccess { _notices.emit(Notice.Info("Backup saved")) }
                .onFailure {
                    _notices.emit(Notice.Error("Backup failed: ${it.message ?: "unknown error"}"))
                }
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val (cats, btns) = backup.import(uri)
                repo.replaceAll(cats, btns)
                refreshAllTiles()
            }
                .onSuccess { _notices.emit(Notice.Info("Backup restored")) }
                .onFailure { e ->
                    val message = when (e) {
                        is BackupManager.BackupFormatException -> e.message
                        else -> "Restore failed: ${e.message ?: "unknown error"}"
                    }
                    _notices.emit(Notice.Error(message ?: "Restore failed"))
                }
        }
    }

    companion object {
        const val DEFAULT_COLUMNS = 4
        const val MIN_COLUMNS = 2
        const val MAX_COLUMNS = 6
    }
}
