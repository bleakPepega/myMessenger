package com.bihnerdranch.example.messenger

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun nextActivity(view: View) {
        val intent = Intent(this@MainActivity, ChatActivity::class.java)
        startActivity(intent)
    }



}