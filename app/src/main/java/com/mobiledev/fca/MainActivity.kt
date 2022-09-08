package com.mobiledev.fca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var classify: CardView
    private lateinit var artStyle: CardView
    private lateinit var superResolution: CardView
    private lateinit var removeBackground: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObjects()
    }
    private fun initObjects() {
        classify = findViewById<CardView>(R.id.classify)
        artStyle = findViewById<CardView>(R.id.artStyle)
        removeBackground = findViewById<CardView>(R.id.removeBackground)
        superResolution = findViewById<CardView>(R.id.superResolution)
        classify.setOnClickListener(this)
        artStyle.setOnClickListener(this)
        removeBackground.setOnClickListener(this)
        superResolution.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.classify -> {
                val intent = Intent(this, Classify::class.java)
                startActivityForResult(intent, 1)
            }
            R.id.artStyle -> {
                val intent = Intent(this, ArtStyle::class.java)
                startActivityForResult(intent, 2)
            }
            R.id.removeBackground ->  {
                val intent = Intent(this, ComingSoon::class.java)
                startActivityForResult(intent, 3)
            }
            R.id.superResolution -> {
                val intent = Intent(this, SuperResolution::class.java)
                startActivityForResult(intent, 4)
            }
            else -> Log.d("Test","fail")
        }
    }

}