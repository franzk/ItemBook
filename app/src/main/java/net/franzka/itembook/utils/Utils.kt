package net.franzka.itembook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.File

class Utils {

    companion object {

        private const val TAG = "Utils"

        fun hideKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun addChipToChipGroup(chipGroup: ChipGroup, name: String, context: Context , clickListener: View.OnClickListener) {
            val chip = Chip(context)
            chip.apply {
                text = name
                tag = name
                isClickable = true
                isFocusable = true
                setOnClickListener(clickListener)
            }
            chipGroup.addView(chip)
        }



        fun resizeImage(file: File, scaleTo: Int = 1024) {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // Determine how much to scale down the image
            val scaleFactor = (photoW / scaleTo).coerceAtMost(photoH / scaleTo)

            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor

            val resized = BitmapFactory.decodeFile(file.absolutePath, bmOptions) ?: return
            file.outputStream().use {
                resized.compress(Bitmap.CompressFormat.JPEG, 75, it)
                resized.recycle()
            }
        }



    }

}