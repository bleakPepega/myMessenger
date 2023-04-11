package com.bihnerdranch.example.messenger
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*

@OptIn(DelicateCoroutinesApi::class)
suspend fun serverHost(): ByteArray? {
    var test = ""
    GlobalScope.launch {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
            .connect(InetSocketAddress("185.242.107.62", 4567))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        output.writeFully("/messages".toByteArray())
        val readyInput = input.readUTF8Line()
        test = readyInput.toString()
        Log.d("testTime", "$test in cor")
        withContext(Dispatchers.IO) {
            socket.close()
        }
    }
    delay(500L)
    Log.d("testTime", "$test well")
    return test.toByteArray()
}
@OptIn(InternalAPI::class)
suspend fun getMessage(combination: MutableList<Byte>, id: String) {
    val json = """
        {
            "message": "$combination"
        }
    """
    val client = HttpClient()
    val response = client.post("http://185.242.107.62:4567/messages/$id") {
        body = json
        contentType(ContentType.Application.Json)
    }
    Log.d("Server Response", "${response.status}")
}
suspend fun postMessage(id: String): List<String> {
    val client = HttpClient()
    val answer = client.get("http://185.242.107.62:4567/messages/$id").body<String>()
    val test = answer.split("][")
    return test

}
suspend fun getingKey(id: String): String {
    val client = HttpClient()
    val answer = client.get("http://185.242.107.62:4567/publicKey/$id").body<String>()
    println(answer)
    return answer
}
@OptIn(InternalAPI::class)
suspend fun sendPublicKey(key: String, id: String) {
    val json = """
        {
            "message": "$key"
        }
    """
    val client = HttpClient()
    val response = client.post("http://185.242.107.62:4567/publicKey/$id") {
        body = json
        contentType(ContentType.Application.Json)
    }
    Log.d("Server Response", "${response.status}")
}