package com.bihnerdranch.example.messenger

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.PrivateKey
import java.security.PublicKey

class ChatActivity:AppCompatActivity() {
    private lateinit var messageButton: Button
    private lateinit var barForMessage: EditText
    private lateinit var message:MutableList<String>
    private lateinit var privateKey: PrivateKey
    private lateinit var publicKey: PublicKey
    private lateinit var testUpdateButton: Button
    init {
        val test = MessageEncryption()
        publicKey = test.publicKey
        privateKey = test.privateKey
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_of_message)
        val messagesRecycler = findViewById<RecyclerView>(R.id.messages_recycler)
        val layoutManager = LinearLayoutManager(this)
        messagesRecycler.layoutManager = layoutManager

        message = mutableListOf()
        messageButton = findViewById(R.id.button_for_send_messages)
        barForMessage = findViewById(R.id.edit_text_for_send_messages)
        testUpdateButton = findViewById(R.id.test_button)
        messageButton.setOnClickListener {
            val encryptedMessage = encryptMessage(barForMessage.text.toString(), publicKey)
            GlobalScope.launch {
                sendMessage(encryptedMessage)
            }
            barForMessage.setText("")
        }
        testUpdateButton.setOnClickListener {
            GlobalScope.launch {
                val decodingMessage = updateMessage()
                delay(500L)
                message.add(decodingMessage)
            }

            messagesRecycler.adapter = MessageAdapter(message)
        }
    }
    suspend fun sendMessage(newMessages: ByteArray?) {
        val test = mutableListOf<Byte>()
        newMessages?.forEach { test.add(it) }
        getMessage(test)
    }
    suspend fun updateMessage(): String {
        val notDecodeMessage = postMessage()
        return decryptMessage(notDecodeMessage, privateKey)
    }
}
