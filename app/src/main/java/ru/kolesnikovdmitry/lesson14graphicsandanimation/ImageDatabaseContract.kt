package ru.kolesnikovdmitry.lesson14graphicsandanimation

import android.provider.BaseColumns

class ImageDatabaseContract {

    class ImageTable : BaseColumns {

        companion object {
            const val TABLE_NAME = "images"

            const val _ID = BaseColumns._ID
            const val COLUMN_NAME = "name"
            const val COLUMN_TIME = "time"
            const val COLUMN_IS_FREE = "is_free"
            const val COLUMN_DATA = "image_data"
        }

    }
}