package net.franzka.itembook.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SaveFile {

    companion object {

        private const val TAG = "SaveFile"
        private const val PREFIX = "IB_"

        private fun  uniqueFilename(extension: String): String {
            return uniqueFilenameWithoutExtension() + "." + extension
        }

       private fun uniqueFilenameWithoutExtension() : String {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            return PREFIX + timeStamp
        }

        private fun viewToBitmap(view: View) : Bitmap {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        fun savePic(image: ImageView, filesDir: String): String? {

            val b = viewToBitmap(image)

            val filename = uniqueFilename("jpg")

            val c = Bitmap.createScaledBitmap(b, b.width, b.height, true)
            val photo = File(filesDir, filename)
            try {
                val os = FileOutputStream(photo)
                c.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
            } catch (e: IOException) {
                Log.e("savePic", "problem copying images", e)
            }
            return photo.absolutePath
        }
    }

    var file: File? = null

    @Throws(IOException::class)
    fun createFile(suffix: String, storageDir: File) {
        // Create an image file name
        file = File.createTempFile(uniqueFilenameWithoutExtension(), suffix, storageDir)
    }

}