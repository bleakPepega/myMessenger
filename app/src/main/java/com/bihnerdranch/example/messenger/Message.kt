package com.bihnerdranch.example.messenger

data class Message(val text: String, val position: Position) {
    enum class Position { LEFT, RIGHT }
}