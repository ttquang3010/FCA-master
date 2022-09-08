package com.mobiledev.fca

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class ComingSoon: AppCompatActivity() {
    private val TAKE_IMAGE_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coming_soon)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                this.TAKE_IMAGE_REQUEST_CODE -> {
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}