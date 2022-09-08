package com.mobiledev.fca

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobiledev.fca.ml.Esrgan

import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.io.FileOutputStream

class SuperResolution:AppCompatActivity() {
    val SEC_ACT_REQ_CODE = 4
    private lateinit var obj: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, ChooseImage::class.java)
        startActivityForResult(intent, SEC_ACT_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SEC_ACT_REQ_CODE -> if (resultCode == RESULT_OK) {

                obj = data?.getParcelableExtra<Bitmap>("BitmapImage")!!
                setContentView(R.layout.super_resolution)
                val btnSave = findViewById<Button>(R.id.btnSave)

                val imageViewOriginal: ImageView = findViewById(R.id.imageview_original)
                val imageViewEnhanced: ImageView = findViewById(R.id.imageview_enhanced)
                imageViewOriginal.setImageBitmap(obj)

                val model = Esrgan.newInstance(this)

                // Creates inputs for reference.
                val originalImage = TensorImage.fromBitmap(obj)

                // Runs model inference and gets result.
                val outputs = model.process(originalImage)
                val enhancedImage = outputs.enhancedImageAsTensorImage
                val enhancedImageBitmap = enhancedImage.bitmap

                btnSave.setOnClickListener {
                    saveImageToExternalStorage(enhancedImageBitmap)
                    Toast.makeText(this,"Saved Successfully",Toast.LENGTH_SHORT).show()
                }

                // Releases model resources if no longer used.
                model.close()
                imageViewEnhanced.setImageBitmap(enhancedImageBitmap)

            }
        }
    }

    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val root: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/saved_image")
        myDir.mkdirs()
        val fname = System.currentTimeMillis().toString() + "_enhanced.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

}