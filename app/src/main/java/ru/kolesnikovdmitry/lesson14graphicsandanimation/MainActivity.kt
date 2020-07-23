package ru.kolesnikovdmitry.lesson14graphicsandanimation

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.YuvImage
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.lang.reflect.Array.getByte
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mConstraintLay : ConstraintLayout
    private var mRadiusCircle = 5F
    private var mColorCircle = Color.RED
    private lateinit var mDraw2D : Draw2D
    private lateinit var mDbHelper : ImageDatabaseHelper

    private val MENU_ITEM_ID_COLOR_RED = 102
    private val MENU_ITEM_ID_COLOR_BLUE = 103
    private val MENU_ITEM_ID_COLOR_BLACK = 104
    private val MENU_ITEM_ID_COLOR_GREEN = 105

    private lateinit var mSeekBarSize : SeekBar
    private lateinit var mLinearLayoutSave : LinearLayout
    private var mIsUsingSeekBar = 0                            //переменная-флаг используется ли SeekBar
    private var mIsUsingLinearLayoutSave = 0


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mConstraintLay = findViewById(R.id.ConstraintLayoutActMain)
        mDraw2D = Draw2D(this)
        mConstraintLay.addView(mDraw2D)

        mSeekBarSize = findViewById(R.id.seekBarSizeActMain)
        mSeekBarSize.progress = mRadiusCircle.toInt()
        mSeekBarSize.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                val curVal = progress.toFloat()
                mRadiusCircle = curVal
                mDraw2D.setSize(mRadiusCircle)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mDbHelper = ImageDatabaseHelper(this)
        }
        else {
            Toast.makeText(this, "Your android version is too old!", Toast.LENGTH_LONG).show()
            finish()
        }

        mLinearLayoutSave = findViewById(R.id.linearLayoutSaveActMain)
        val btnSave : Button = findViewById(R.id.buttonSaveActMain)
        val editTextSave : EditText = findViewById(R.id.editTextSaveActMain)
        btnSave.setOnClickListener {v: View? ->
            saveImage(editTextSave)
        }

    }

    private fun saveImage(editTextSave: EditText) {
        //Сохранение картинки
        val curName : String = editTextSave.text.toString()
        //узнаем дату и время
        val curDate : Date = Date()
        //делаем форматы для времени и даты
        val timeFormat : SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) //Время
        val dateFormat : SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) //Date
        //make string time and Date
        val curTimeString : String = timeFormat.format(curDate) + dateFormat.format(curDate)
        val curBitmap = mDraw2D.getBitmap()

        if (curName == "") {
            Snackbar.make(editTextSave, "Please, enter name of your picture!", Snackbar.LENGTH_LONG).show()
            return
        }
        //Выборка
        val projections = arrayOf(ImageDatabaseContract.ImageTable._ID, ImageDatabaseContract.ImageTable.COLUMN_IS_FREE)
        val db : SQLiteDatabase = mDbHelper.writableDatabase
        val cursor : Cursor = db.query(
            ImageDatabaseContract.ImageTable.TABLE_NAME,
            projections,
            null,
            null,
            null,
            null,
            null
        )
        //Date for insert:
        val values = ContentValues()
        values.put(ImageDatabaseContract.ImageTable.COLUMN_TIME, curTimeString)
        values.put(ImageDatabaseContract.ImageTable.COLUMN_IS_FREE, 0)
        values.put(ImageDatabaseContract.ImageTable.COLUMN_DATA, getByte(curBitmap))
        values.put(ImageDatabaseContract.ImageTable.COLUMN_NAME, curName)

        try {
            val idColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable._ID)
            val isFreeColIndex = cursor.getColumnIndex(ImageDatabaseContract.ImageTable.COLUMN_IS_FREE)

            while (cursor.moveToNext()) {
                val curIsFree = cursor.getInt(isFreeColIndex)
                val curID = cursor.getInt(idColIndex)

                if (curIsFree == 1) {
                    db.update(ImageDatabaseContract.ImageTable.TABLE_NAME, values, " _ID = ? ", arrayOf(curID.toString()))
                    Snackbar.make(editTextSave, "Your picture was saved successfully!", Snackbar.LENGTH_LONG).show()
                    hideSaveWindow()
                    mIsUsingLinearLayoutSave = 0
                    //обновляем холст
                    mDraw2D = Draw2D(this)
                    mConstraintLay.removeAllViews()
                    mConstraintLay.addView(mDraw2D)
                    cursor.close()
                    return
                }
            }

            db.insert(ImageDatabaseContract.ImageTable.TABLE_NAME, null, values)
            hideSaveWindow()
            Snackbar.make(editTextSave, "Your picture was saved successfully!", Snackbar.LENGTH_LONG).show()
            mIsUsingLinearLayoutSave = 0
            //обновляем холст
            mDraw2D = Draw2D(this)
            mConstraintLay.removeAllViews()
            mConstraintLay.addView(mDraw2D)
            cursor.close()
            return
        } catch (th : Throwable) {
            Toast.makeText(this, th.message, Toast.LENGTH_LONG).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            MENU_ITEM_ID_COLOR_RED -> {
                item.isChecked = !item.isCheckable
                mColorCircle = Color.RED
                mDraw2D.setColor(mColorCircle)
                return true
            }
            MENU_ITEM_ID_COLOR_GREEN -> {
                item.isChecked = !item.isCheckable
                mColorCircle = Color.GREEN
                mDraw2D.setColor(mColorCircle)
                return true
            }
            MENU_ITEM_ID_COLOR_BLACK -> {
                item.isChecked = !item.isCheckable
                mColorCircle = Color.BLACK
                mDraw2D.setColor(mColorCircle)
                return true
            }
            MENU_ITEM_ID_COLOR_BLUE -> {
                item.isChecked = !item.isCheckable
                mColorCircle = Color.BLUE
                mDraw2D.setColor(mColorCircle)
                return true
            }
            R.id.menuItemSizeActMain -> {
                if (mIsUsingSeekBar == 0) {
                    item.setIcon(R.drawable.ic_baseline_check_24)
                    showSeekBar()
                    mIsUsingSeekBar = 1
                }
                else {
                    mIsUsingSeekBar = 0
                    hideSeekBar()
                    item.setIcon(R.drawable.ic_baseline_format_size_24)
                }
            }
            R.id.menuItemSave -> {
                if(mIsUsingLinearLayoutSave == 0) {
                    mIsUsingLinearLayoutSave = 1
                    showSaveWindow()
                }
                else {
                    mIsUsingLinearLayoutSave = 0
                    hideSaveWindow()
                }
            }
            R.id.menuItemGallery -> {
                val intent : Intent = Intent(this, GalleryActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideSaveWindow() {
        //TODO: make animation
        mLinearLayoutSave.visibility = View.INVISIBLE
    }

    private fun showSaveWindow() {
        //TODO: make animation
        mLinearLayoutSave.visibility = View.VISIBLE
    }

    private fun hideSeekBar() {
        //TODO: Make animation
        mSeekBarSize.visibility = View.INVISIBLE
    }

    private fun showSeekBar() {
        //TODO: make animation
        mSeekBarSize.visibility = View.VISIBLE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val submenuColor = menu!!.addSubMenu("Color")
        submenuColor.add(101, MENU_ITEM_ID_COLOR_RED, Menu.NONE, "Red").isChecked = true
        submenuColor.add(101, MENU_ITEM_ID_COLOR_BLUE, Menu.NONE, "Blue")
        submenuColor.add(101, MENU_ITEM_ID_COLOR_BLACK, Menu.NONE, "Black")
        submenuColor.add(101, MENU_ITEM_ID_COLOR_GREEN, Menu.CATEGORY_ALTERNATIVE, "Green")
        submenuColor.setGroupCheckable(101, true, true)

        return true
    }

    //from bitmap to byte array
    fun getByte(bitmap: Bitmap) : ByteArray {
        val stream : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    //from byteArray to bitmap
    fun getBitMap(byteArr : ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
    }

}