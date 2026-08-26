package org.everyvoice.aac.data

import androidx.room.withTransaction
import org.everyvoice.aac.engine.Vocabulary

/**
 * Single door into storage. Seeding is idempotent: it runs only when the
 * categories table is empty, so an updated app never clobbers a caregiver's
 * customized vocabulary.
 */
class Repository(private val db: AacDatabase) {

    private val categories = db.categoryDao()
    private val buttons = db.buttonDao()

    fun observeCategories() = categories.observeAll()

    fun observeButtons(categoryId: String) = buttons.observeByCategory(categoryId)

    suspend fun ensureSeeded() {
        if (categories.count() > 0) return

        val seed = Vocabulary.seed()
        categories.insertAll(
            seed.mapIndexed { index, cat ->
                CategoryEntity(
                    id = cat.id,
                    name = cat.name,
                    icon = cat.icon,
                    sortOrder = index,
                )
            }
        )
        seed.forEach { cat ->
            buttons.insertAll(
                cat.tiles.mapIndexed { index, tile ->
                    ButtonEntity(
                        categoryId = cat.id,
                        label = tile.label,
                        speakText = tile.speakText,
                        isCustom = false,
                        sortOrder = index,
                    )
                }
            )
        }
    }

    suspend fun addButton(
        categoryId: String,
        label: String,
        speakText: String,
        imagePath: String?,
        icon: String,
    ): Long {
        val nextOrder = buttons.maxSortOrder(categoryId) + 1
        return buttons.insert(
            ButtonEntity(
                categoryId = categoryId,
                label = label.trim(),
                speakText = speakText.trim().ifEmpty { label.trim() },
                imagePath = imagePath,
                icon = icon,
                isCustom = true,
                sortOrder = nextOrder,
            )
        )
    }

    suspend fun updateButton(button: ButtonEntity) = buttons.update(button)

    suspend fun deleteButton(button: ButtonEntity) = buttons.delete(button)

    /** Everything, for backup. */
    suspend fun exportData(): Pair<List<CategoryEntity>, List<ButtonEntity>> =
        categories.all() to buttons.all()

    /**
     * Full restore: wipes and reinserts in one transaction. Callers run this
     * only after the backup file has parsed completely, so a corrupt file
     * never leaves an empty database.
     *
     * Categories must be deleted, not merged. Room generates
     * `INSERT OR ABORT` for a plain `@Insert`, so re-inserting a seeded id
     * like "core" raised a UNIQUE constraint violation and rolled the whole
     * restore back — every restore failed on every device. Deleting first
     * also drops categories created after the backup was taken, which would
     * otherwise survive as empty orphans once their buttons were wiped.
     */
    suspend fun replaceAll(
        newCategories: List<CategoryEntity>,
        newButtons: List<ButtonEntity>,
    ) {
        db.withTransaction {
            buttons.deleteAll()
            categories.deleteAll()
            categories.insertAll(newCategories)
            buttons.insertAll(newButtons)
        }
    }
}
