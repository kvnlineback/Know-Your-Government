package com.example.knowyourgovernment

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val text = findViewById<TextView>(R.id.textView3)
        text.paintFlags = text.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    fun googleAPI(v: View?) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://developers.google.com/civic-information/"))
        startActivity(i)
    }
}