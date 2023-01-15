package com.bihnerdranch.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun nextActivity(view: View) {
        val intent = Intent(this@MainActivity, ChatActivity::class.java)
        startActivity(intent)
    }
}