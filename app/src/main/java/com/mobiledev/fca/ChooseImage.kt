package com.mobiledev.fca

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ChooseImage: AppCompatActivity() {
    lateinit var bitMap: Bitmap
    private val GALLERY_REQUEST_CODE = 123
    private val TAKE_IMAGE_REQUEST_CODE = 122
    //lateinit var uri : URI
    private val PERMISSION_ALL = 1

    private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_image)

        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        initObject()
    }

    private fun initObject() {
        val mCamera = findViewById<CardView>(R.id.camera)
        val mGallery = findViewById<CardView>(R.id.gallery)

        mCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, this.TAKE_IMAGE_REQUEST_CODE)
        }

        mGallery.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(
                Intent.createChooser(intent, "Pick an image"),
                this.GALLERY_REQUEST_CODE
            )
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null ) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                this.GALLERY_REQUEST_CODE -> {
                    val selectedImage = data?.data

                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedImage
                        )
                        bitMap = bitmap
                        val intent = intent
                        intent.putExtra("BitmapImage", bitMap)
                        setResult(RESULT_OK, intent)
                        finish()
                    } catch (e: IOException) {
                        Log.i("ChooseImage", "Some exception $e")
                    }
                }
                this.TAKE_IMAGE_REQUEST_CODE -> {
                    val bitmap = data?.extras!!["data"] as Bitmap
                    bitMap = bitmap
                    saveImageToExternalStorage(bitmap)
                    val intent = intent
                    intent.putExtra("BitmapImage", bitMap)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }


    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val root: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/saved_image")
        myDir.mkdirs()
        val fname = System.currentTimeMillis().toString() + ".jpg"
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