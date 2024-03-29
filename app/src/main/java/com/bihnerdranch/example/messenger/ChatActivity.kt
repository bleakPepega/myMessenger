package com.bihnerdranch.example.messenger

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bihnerdranch.example.messenger.HotelContract.GuestEntry
import com.bihnerdranch.example.messenger.data.DataBase
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import kotlin.io.use


@RequiresApi(Build.VERSION_CODES.O)
class ChatActivity:AppCompatActivity() {
    private lateinit var messageButton: Button
    private lateinit var barForMessage: EditText
    private lateinit var message: MutableList<Message>
    private lateinit var myMessges: MutableList<Message>
    private lateinit var privateKey: PrivateKey
    private lateinit var publicKey: PublicKey
    private lateinit var testUpdateButton: Button
    private lateinit var testButtonForTable: Button
    private lateinit var deleteButton: Button
    private lateinit var getButton: Button
    private lateinit var sendButton: Button
    private lateinit var context: Context
    private lateinit var arrayOfKeys: MutableList<String>
    private var id: String = "1"
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_of_message)
        val messagesRecycler = findViewById<RecyclerView>(R.id.messages_recycler)
        val layoutManager = LinearLayoutManager(this)
        val adapter = MessageAdapter(mutableListOf())
        messagesRecycler.adapter = adapter

        messagesRecycler.layoutManager = layoutManager

        message = mutableListOf<Message>()
        myMessges = mutableListOf()
        messageButton = findViewById(R.id.button_for_send_messages)
        barForMessage = findViewById(R.id.edit_text_for_send_messages)
        testUpdateButton = findViewById(R.id.test_button)

//        testButtonForTable = findViewById(R.id.button)
        context = applicationContext
        arrayOfKeys = AppealToDataBase(context).searchKeyInTable()
        privateKey = MessageEncryption().toPrivateKey(arrayOfKeys[1])!!
        publicKey = MessageEncryption().toPublicKey(arrayOfKeys[0])!!
        messageButton.setOnClickListener {
            val text = barForMessage.text.toString()
            myMessges.add(Message(text, Message.Position.LEFT))
            adapter.messages.add(Message(text, Message.Position.LEFT))
            adapter.notifyDataSetChanged()
            val encryptedMessage = encryptMessage(text, publicKey)
            GlobalScope.launch {
                sendMessage(encryptedMessage)
            }
            barForMessage.setText("")
        }
        testUpdateButton.setOnClickListener {
            GlobalScope.launch {
                val decodingMessage = updateMessage()
                delay(500L)
                runOnUiThread {
                    decodingMessage.forEach { adapter.messages.add(Message(it, Message.Position.RIGHT)) }
                    adapter.notifyDataSetChanged()
                }
            }
            Log.d("q", message.toString())
//            adapter.messages.add(Message(message, Message.Position.RIGHT))
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_get_key -> {
                GlobalScope.launch {
                    val newPublicKey = getingKey("1")
                    MessageEncryption().toPublicKey(newPublicKey)
                        ?.let { it1 -> AppealToDataBase(context).updateValue(arrayOfKeys[0], it1) }
                    arrayOfKeys = AppealToDataBase(context).searchKeyInTable()
                    privateKey = MessageEncryption().toPrivateKey(arrayOfKeys[1])!!
                    publicKey = MessageEncryption().toPublicKey(arrayOfKeys[0])!!
                }
                Toast.makeText(this, "Ключ получен", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_send_key -> {
                GlobalScope.launch {
                    sendPublicKey(arrayOfKeys[0], id)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ChatActivity, "Ключ отправлен", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            R.id.action_delete -> {
                AppealToDataBase(context).deleteDB()
                Toast.makeText(this, "Ключи удалены", Toast.LENGTH_SHORT).show()
                arrayOfKeys = AppealToDataBase(context).searchKeyInTable()
                privateKey = MessageEncryption().toPrivateKey(arrayOfKeys[1])!!
                publicKey = MessageEncryption().toPublicKey(arrayOfKeys[0])!!
                true
            }
            else -> {
                Toast.makeText(this, "Вот так вот", Toast.LENGTH_SHORT).show()

                super.onOptionsItemSelected(item)
            }
        }
    }


    private suspend fun sendMessage(newMessages: ByteArray?) {
        val test = mutableListOf<Byte>()
        newMessages?.forEach { test.add(it) }
        getMessage(test, id)
    }

    private suspend fun updateMessage(): List<String> {
        val notDecodeMessage = postMessage(id)
        val test = notDecodeMessage.map { it
            .trim('[', ']')
            .split(", ")
            .map { str -> str.toByte() }
            .toByteArray() }.map {
                decryptMessage( it, privateKey)
        }
        Log.d("testMap", test.toString())
        return test
    }


}
class AppealToDataBase(val context: Context) {
    @RequiresApi(Build.VERSION_CODES.O)
     fun insertValue() {
        val database = DataBase(context).writableDatabase
        val messageEncryption = MessageEncryption()
        val privateKey = messageEncryption.privateKey
        val publicKey = messageEncryption.publicKey
        val values = ContentValues().apply {
            put(GuestEntry.primaryKey, "${messageEncryption.keyToString(privateKey)}")
            put(GuestEntry.publicKey, "${messageEncryption.keyToString(publicKey)}")
        }
        val newRowId = database.insert(GuestEntry.NAME, null, values)
    }

    private fun readValue(): MutableList<String> {
        var primaryKeyString: String = ""
        var publicKeyString: String = ""
        val database = DataBase(context).readableDatabase
        val projection = arrayOf(GuestEntry.ID, GuestEntry.primaryKey, GuestEntry.publicKey)
        val cursor = database.query(
            GuestEntry.NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(GuestEntry.ID))
                    primaryKeyString = it.getString(it.getColumnIndexOrThrow(GuestEntry.primaryKey))
                    publicKeyString = it.getString(it.getColumnIndexOrThrow(GuestEntry.publicKey))
                    Log.d("TEST TABLE", "$id, $primaryKeyString, $publicKeyString")
                } while (it.moveToNext())
            }
        }
        return mutableListOf(publicKeyString, primaryKeyString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchKeyInTable(): MutableList<String> {
        val listWithKey = readValue()
        println(listWithKey[0])
        if (listWithKey[0] != "" && listWithKey[1] != "") return listWithKey
        else {
            insertValue()
            return readValue()
        }
    }

     fun deleteDB() {
        val database = DataBase(context).writableDatabase
        database.delete(GuestEntry.NAME, null, null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
     fun updateValue(oldKey: String, publicKey: PublicKey) {
        val database = DataBase(context).writableDatabase
        val messageEncryption = MessageEncryption()
        Log.d("public", publicKey.toString())
        val values = ContentValues().apply {
            put(GuestEntry.publicKey, "${messageEncryption.keyToString(publicKey)}")
        }
        val selection = "${GuestEntry.ID} = ?"
        println(GuestEntry.ID)
        val selectionArgs = arrayOf(getFirstID().toString())
        val updatedRows = database.update(GuestEntry.NAME, values, selection, selectionArgs)
    }
    @SuppressLint("Range")
    fun getFirstID(): Int {
        val database = DataBase(context).readableDatabase

        // Создаем SQL-запрос для выборки записей с сортировкой по ID
        val selectQuery = "SELECT * FROM ${GuestEntry.NAME} ORDER BY ${GuestEntry.ID} ASC LIMIT 1"
        val cursor = database.rawQuery(selectQuery, null)

        // Получаем первое значение ID, если оно существует
        val firstID = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(GuestEntry.ID))
        } else {
            1
        }

        cursor.close()
        println(firstID)
        return firstID
    }

}
