package com.bihnerdranch.example.messenger.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bihnerdranch.example.messenger.HotelContract.GuestEntry


class DataBase(context: Context) : SQLiteOpenHelper(context, "TABLE_KEY", null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val SQL_CREATE_GUESTS_TABLE =
            ("CREATE TABLE " + GuestEntry.NAME + " (" + GuestEntry.ID.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GuestEntry.primaryKey + " TEXT NOT NULL, "
                    + GuestEntry.publicKey + " TEXT NOT NULL);")
        p0!!.execSQL(SQL_CREATE_GUESTS_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}
