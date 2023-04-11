package com.bihnerdranch.example.messenger

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(internal val messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        when (message.position) {
            Message.Position.LEFT -> {
                holder.messageTextViewLeft.text = message.text
                holder.messageTextViewRight.text = ""
                holder.messageTextViewRight.setBackgroundColor(Color.WHITE)
            }
            Message.Position.RIGHT -> {
                holder.messageTextViewLeft.text = ""
                holder.messageTextViewLeft.setBackgroundColor(Color.WHITE)
                holder.messageTextViewRight.text = message.text
            }
        }

    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextViewLeft: TextView = view.findViewById<TextView>(R.id.messages_textview_left)
        val messageTextViewRight: TextView = view.findViewById<TextView>(R.id.messages_textview_right)


    }
}

