package ru.kolesnikovdmitry.lesson14graphicsandanimation

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class ImageDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_IMAGE_TABLE_STRING = " CREATE TABLE " + ImageDatabaseContract.ImageTable.TABLE_NAME + " ( " +
                ImageDatabaseContract.ImageTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ImageDatabaseContract.ImageTable.COLUMN_NAME + " TEXT DEFAULT \"\", " +
                ImageDatabaseContract.ImageTable.COLUMN_TIME + " TEXT DEFAULT \"0\", " +
                ImageDatabaseContract.ImageTable.COLUMN_DATA + " BLOB, " +
                ImageDatabaseContract.ImageTable.COLUMN_IS_FREE + " INTEGER NOT NULL DEFAULT 1 ); "

        db!!.execSQL(CREATE_IMAGE_TABLE_STRING)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val DELETE_IMAGE_TABLE_STRING = "DROP TABLE IF EXISTS " + ImageDatabaseContract.ImageTable.TABLE_NAME + ";"
        db!!.execSQL(DELETE_IMAGE_TABLE_STRING)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = ImageDatabaseContract.ImageTable.TABLE_NAME
        const val DATABASE_VERSION = 3
    }
}