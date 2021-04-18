package net.franzka.itembook.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class ImageHelper {

    companion object {
        const val RC_CAMERA = 0
        const val RC_GALLERY = 1
    }

    private val imageFile = SaveFile()

    fun camera(context: Context, filesDir: File): Intent? {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also { // TODO resolve machoin :!!!!!!
                try {
                    imageFile.createFile("jpg", filesDir)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }

                // Continue only if the File was successfully created
                imageFile.file?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "net.franzk.itembook.provider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //startActivityForResult(takePictureIntent, SpaceFormFragment.RC_CAMERA)
                    return takePictureIntent
                }
            }
        }
        return null
    }

    fun getImageFile(): File? {
        return imageFile.file
    }


    fun gallery(): Intent{
        return Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
    }

}