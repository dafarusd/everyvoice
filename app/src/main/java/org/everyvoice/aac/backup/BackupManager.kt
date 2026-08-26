package org.everyvoice.aac.backup

import android.content.Context
import android.net.Uri
import org.everyvoice.aac.data.ButtonEntity
import org.everyvoice.aac.data.CategoryEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Backup and restore.
 *
 * A customized vocabulary is months of caregiver labor. Losing it to a
 * broken phone is not an inconvenience; it is a child losing their voice
 * twice. So backups are a first-class feature, not a settings footnote.
 *
 * Format: a ZIP containing `vocabulary.json` plus every referenced image.
 * JSON over a database dump so backups survive schema changes and can be
 * hand-inspected.
 */
class BackupManager(private val context: Context) {

    class BackupFormatException(message: String) : Exception(message)

    suspend fun export(uri: Uri, data: Pair<List<CategoryEntity>, List<ButtonEntity>>) {
        val (cats, btns) = data

        val root = JSONObject()
        root.put("format", FORMAT_ID)
        root.put("version", FORMAT_VERSION)

        val catArray = JSONArray()
        for (c in cats) {
            catArray.put(JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("icon", c.icon)
                put("sortOrder", c.sortOrder)
            })
        }
        root.put("categories", catArray)

        val imageFiles = mutableMapOf<String, File>()
        val btnArray = JSONArray()
        for (b in btns) {
            var imageName: String? = null
            val path = b.imagePath
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    imageName = "images/${b.id}_${file.name}"
                    imageFiles[imageName] = file
                }
            }
            btnArray.put(JSONObject().apply {
                put("id", b.id)
                put("categoryId", b.categoryId)
                put("label", b.label)
                put("speakText", b.speakText)
                put("image", imageName ?: JSONObject.NULL)
                put("icon", b.icon)
                put("isCustom", b.isCustom)
                put("sortOrder", b.sortOrder)
            })
        }
        root.put("buttons", btnArray)

        val resolver = context.contentResolver
        resolver.openOutputStream(uri)?.use { out ->
            ZipOutputStream(out.buffered()).use { zip ->
                zip.putNextEntry(ZipEntry(JSON_ENTRY))
                zip.write(root.toString().toByteArray(Charsets.UTF_8))
                zip.closeEntry()

                for ((entryName, file) in imageFiles) {
                    zip.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
            }
        } ?: throw IOException("Could not open backup destination")
    }

    suspend fun import(uri: Uri): Pair<List<CategoryEntity>, List<ButtonEntity>> {
        val resolver = context.contentResolver
        var json: JSONObject? = null
        val images = mutableMapOf<String, ByteArray>()

        resolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input.buffered()).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    if (entry.name == JSON_ENTRY) {
                        json = JSONObject(zip.readBytes().toString(Charsets.UTF_8))
                    } else if (entry.name.startsWith("images/")) {
                        images[entry.name] = zip.readBytes()
                    }
                    zip.closeEntry()
                }
            }
        } ?: throw BackupFormatException("Could not open backup file")

        val root = json ?: throw BackupFormatException("Backup has no vocabulary.json")
        if (root.optString("format") != FORMAT_ID) {
            throw BackupFormatException("Not an EveryVoice backup")
        }

        val catArray = root.optJSONArray("categories")
            ?: throw BackupFormatException("Backup has no categories")
        val btnArray = root.optJSONArray("buttons")
            ?: throw BackupFormatException("Backup has no buttons")

        val cats = buildList {
            for (i in 0 until catArray.length()) {
                val o = catArray.getJSONObject(i)
                add(
                    CategoryEntity(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        icon = o.optString("icon", ""),
                        sortOrder = o.optInt("sortOrder", i),
                    )
                )
            }
        }

        val imageDir = File(context.filesDir, IMAGES_DIR).apply { mkdirs() }
        val btns = buildList {
            for (i in 0 until btnArray.length()) {
                val o = btnArray.getJSONObject(i)

                var imagePath: String? = null
                val imageName = if (o.isNull("image")) null else o.getString("image")
                if (imageName != null && images.containsKey(imageName)) {
                    val safeName = File(imageName).name
                    val dest = File(imageDir, safeName)
                    dest.writeBytes(images.getValue(imageName))
                    imagePath = dest.absolutePath
                }

                // optString returns "" for a key that is present but empty,
                // not the fallback — so a hand-edited backup could restore a
                // tile that says nothing and crashed the sentence strip.
                val label = o.getString("label")
                val speakText = o.optString("speakText", "").ifBlank { label }

                add(
                    ButtonEntity(
                        // New ids are assigned on insert so restores never
                        // collide with leftover auto-increment state.
                        id = 0,
                        categoryId = o.getString("categoryId"),
                        label = label,
                        speakText = speakText,
                        imagePath = imagePath,
                        icon = o.optString("icon", ""),
                        isCustom = o.optBoolean("isCustom", false),
                        sortOrder = o.optInt("sortOrder", i),
                    )
                )
            }
        }

        return cats to btns
    }

    companion object {
        private const val FORMAT_ID = "org.everyvoice.backup"
        private const val FORMAT_VERSION = 1
        private const val JSON_ENTRY = "vocabulary.json"
        const val IMAGES_DIR = "tile_images"
    }
}
