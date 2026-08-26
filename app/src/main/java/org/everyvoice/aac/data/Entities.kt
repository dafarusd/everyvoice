package org.everyvoice.aac.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.everyvoice.aac.engine.Tile

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String,
    val sortOrder: Int,
)

@Entity(
    tableName = "buttons",
    indices = [Index("categoryId")],
)
data class ButtonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: String,
    val label: String,
    val speakText: String,
    /** Absolute path inside app-private storage, or null for icon/text tiles. */
    val imagePath: String? = null,
    /** Emoji shown on tiles without a photo. Empty string = no icon. */
    val icon: String = "",
    /** True for tiles the caregiver created; seeded tiles are false. */
    val isCustom: Boolean = false,
    val sortOrder: Int,
)

fun ButtonEntity.toTile(): Tile = Tile(label = label, speakText = speakText, id = id)
