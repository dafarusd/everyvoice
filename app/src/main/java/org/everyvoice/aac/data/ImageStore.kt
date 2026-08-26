package org.everyvoice.aac.data

import android.content.Context
import android.graphics.Bitmap
import org.everyvoice.aac.backup.BackupManager
import java.io.File
import java.util.UUID

/**
 * Saves caregiver-taken photos into app-private storage.
 *
 * Photos live in internal storage: no permissions, invisible to other apps,
 * and wiped on uninstall along with everything else.
 */
class ImageStore(context: Context) {

    private val dir = File(context.filesDir, BackupManager.IMAGES_DIR).apply { mkdirs() }

    fun save(bitmap: Bitmap): String {
        val file = File(dir, "${UUID.randomUUID()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        return file.absolutePath
    }

    fun delete(path: String?) {
        if (path == null) return
        val file = File(path)
        // Only ever delete files this app created.
        if (file.parentFile == dir) file.delete()
    }

    private companion object {
        const val JPEG_QUALITY = 85
    }
}
