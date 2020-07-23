package ru.kolesnikovdmitry.lesson14graphicsandanimation

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity: AppCompatActivity() {

    private lateinit var mDbHelper: ImageDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)


        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Gallery"
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mDbHelper = ImageDatabaseHelper(this)
        }
        try {
            displayImages()
        } catch (th : Throwable) {
            Toast.makeText(this, th.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun displayImages() {
        val db : SQLiteDatabase = mDbHelper.readableDatabase
        val projections = arrayOf(
            ImageDatabaseContract.ImageTable._ID,
            ImageDatabaseContract.ImageTable.COLUMN_NAME,
            ImageDatabaseContract.ImageTable.COLUMN_TIME,
            ImageDatabaseContract.ImageTable.COLUMN_IS_FREE,
            ImageDatabaseContract.ImageTable.COLUMN_DATA)
        val cursor : Cursor = db.query(
            ImageDatabaseContract.ImageTable.TABLE_NAME,
            projections,
            null,
            null,
            null,
            null,
            null
        )

        try {
            val idColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable._ID)
            val isFreeColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable.COLUMN_IS_FREE)
            val dataColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable.COLUMN_DATA)
            val nameColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable.COLUMN_NAME)
            val timeColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable.COLUMN_TIME)

            while (cursor.moveToNext()) {
                val curIsFree = cursor.getInt(isFreeColIndex)
                //если в этой ячейке не пусто, то читаем, иначе не читаем ее
                if (curIsFree == 0) {
                    val curName = cursor.getString(nameColIndex)
                    val curTime = cursor.getString(timeColIndex)
                    val curData = cursor.getBlob(dataColIndex)
                    //добавляем на экран карточку с картинкой
                    addNewImageCard(curName, curTime, curData)
                }
            }
        } catch (th : Throwable) {
            Toast.makeText(this, th.message, Toast.LENGTH_LONG).show()
        }
        cursor.close()
    }

    private fun addNewImageCard(curName: String?, curTime: String?, curData: ByteArray?) {
        //в этот лейаут мы будем вставлять другие, тем самым формируя список
        val linearLayoutCards : LinearLayout = findViewById(R.id.linearLayoutCardsActGallery)
        //в этот мы будем вставлять данные
        val workLinearLayForInsert : View = LayoutInflater.from(this).inflate(R.layout.card_activity_gallery, linearLayoutCards, false)
        //находим наши элементы
        val textViewName : TextView = workLinearLayForInsert.findViewById(R.id.textViewNameActGallery)
        val textViewTime : TextView = workLinearLayForInsert.findViewById(R.id.textViewTimeActGallery)
        val imageViewPic : ImageView = workLinearLayForInsert.findViewById(R.id.imageViewCardActGallery)
        //вставляем данные
        textViewName.text = curName
        textViewTime.text = ("This picture was painted at " + curTime!!.dropLast(10) + " on " + curTime.drop(8) + ".")
        imageViewPic.setImageBitmap(getBitMap(curData))
        linearLayoutCards.addView(workLinearLayForInsert)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //from bitmap to byte array
    fun getByte(bitmap: Bitmap) : ByteArray {
        val stream : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    //from byteArray to bitmap
    fun getBitMap(byteArr : ByteArray?) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr!!.size)
    }
}