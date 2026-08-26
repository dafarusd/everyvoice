package org.everyvoice.aac.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

/**
 * Turning a camera capture into a tile picture.
 *
 * The camera writes a full-resolution JPEG into our own cache directory and
 * we decode it ourselves. The alternative — asking the camera for a preview
 * bitmap — hands back roughly 190x250 pixels, which looked acceptable at four
 * columns and soft at two, and two columns is the setting someone with poor
 * motor control actually needs.
 */
object PhotoCapture {

    /** Longest edge we keep. Big enough for a full-width tile, small enough
     *  that a cheap phone never holds a 12-megapixel bitmap in memory. */
    private const val MAX_EDGE = 1024

    private const val TAG = "PhotoCapture"

    /** A fresh file in our cache plus the content URI the camera can write to. */
    fun newCaptureTarget(context: Context): Pair<File, Uri> {
        val dir = File(context.cacheDir, "captures").apply { mkdirs() }
        val file = File.createTempFile("capture_", ".jpg", dir)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        return file to uri
    }

    /**
     * Reads [file] into a bitmap that is downsampled to [MAX_EDGE] and turned
     * the right way up, then deletes it. Returns null if the capture was
     * cancelled or the file cannot be decoded.
     */
    fun decodeAndClear(file: File): Bitmap? {
        if (!file.exists() || file.length() == 0L) {
            file.delete()
            return null
        }
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

            var sample = 1
            while (bounds.outWidth / sample > MAX_EDGE || bounds.outHeight / sample > MAX_EDGE) {
                sample *= 2
            }

            val decoded = BitmapFactory.decodeFile(
                file.absolutePath,
                BitmapFactory.Options().apply { inSampleSize = sample },
            ) ?: return null

            rotateToUpright(decoded, file)
        } catch (e: OutOfMemoryError) {
            // A cheap phone with a big camera. Better a tile with no picture
            // than a crash in the middle of making one.
            Log.w(TAG, "Out of memory decoding a capture", e)
            null
        } finally {
            file.delete()
        }
    }

    /**
     * Cameras record which way the phone was held rather than rotating the
     * pixels, so a photo taken in portrait decodes on its side unless the
     * EXIF tag is applied.
     */
    private fun rotateToUpright(bitmap: Bitmap, file: File): Bitmap {
        val orientation = try {
            ExifInterface(file.absolutePath)
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } catch (e: Exception) {
            Log.w(TAG, "Could not read EXIF orientation", e)
            ExifInterface.ORIENTATION_NORMAL
        }

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                .also { if (it != bitmap) bitmap.recycle() }
        } catch (e: OutOfMemoryError) {
            Log.w(TAG, "Out of memory rotating a capture", e)
            bitmap
        }
    }
}
