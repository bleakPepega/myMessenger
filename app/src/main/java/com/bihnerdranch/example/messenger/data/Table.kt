package com.bihnerdranch.example.messenger

import android.provider.BaseColumns


class HotelContract private constructor() {
    object GuestEntry : BaseColumns {
        const val NAME = "tableOFKey"
        const val publicKey = "publicKey"
        const val primaryKey = "primaryKey"
        const val ID = BaseColumns._ID
    }
}