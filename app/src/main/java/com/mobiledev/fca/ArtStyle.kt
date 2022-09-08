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
import com.mobiledev.fca.ml.WhiteboxCartoonGanFp16
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.io.FileOutputStream

class ArtStyle:AppCompatActivity() {

    val SEC_ACT_REQ_CODE = 2
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
            SEC_ACT_REQ_CODE -> if (resultCode == AppCompatActivity.RESULT_OK) {

                obj = data?.getParcelableExtra<Bitmap>("BitmapImage")!!
                setContentView(R.layout.art_style)

                val btnSave = findViewById<Button>(R.id.btnSave)
                val model = WhiteboxCartoonGanFp16.newInstance(this)

                // Runs model inference and gets result.
                val originalImage = TensorImage.fromBitmap(obj)
                val outputs = model.process(originalImage)
                val cartoonizedImage = outputs.cartoonizedImageAsTensorImage

                btnSave.setOnClickListener {
                    saveImageToExternalStorage(cartoonizedImage.bitmap)
                }

                val imageViewOriginal: ImageView = findViewById(R.id.imageview_original)
                val imageViewArt: ImageView = findViewById(R.id.imageview_art)
                // Releases model resources if no longer used.
                model.close()
                imageViewOriginal.setImageBitmap(obj)
                imageViewArt.setImageBitmap(cartoonizedImage.bitmap)

            }
        }
    }

    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val root: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/saved_image")
        myDir.mkdirs()
        val fname = System.currentTimeMillis().toString() + "_art.jpg"
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
        Toast.makeText(this,"Saved Successfully", Toast.LENGTH_SHORT).show()
    }
}