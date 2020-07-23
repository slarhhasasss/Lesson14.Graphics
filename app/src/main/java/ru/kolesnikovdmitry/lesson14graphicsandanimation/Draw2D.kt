package ru.kolesnikovdmitry.lesson14graphicsandanimation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

public class Draw2D(context: Context?) : View(context) {

    private var mDrawPath : Path = Path()
    private var mDrawPaint : Paint = Paint()
    private lateinit var mCanvasPaint : Paint
    private lateinit var mDrawCanvas : Canvas
    private lateinit var mCanvasBitmap : Bitmap
    private var mColorCircle = Color.RED

    //Конструктор класса
    init {
        mDrawPaint.color = mColorCircle
        mDrawPaint.isAntiAlias = true
        mDrawPaint.strokeWidth = 20F    //размер линии
        mDrawPaint.style = Paint.Style.STROKE             //без этого будут не линии, а какая-то фигня
        //mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND          //без этого параметра не ставятся точки, только если двигать пальцем

        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    fun setColor(color : Int) {
        mColorCircle = color
        mDrawPaint.color = mColorCircle
    }

    fun setSize(size : Float) {
        mDrawPaint.strokeWidth = size
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap =  Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mDrawCanvas = Canvas(mCanvasBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawBitmap(mCanvasBitmap, 0F, 0F, mCanvasPaint)
        canvas.drawPath(mDrawPath, mDrawPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val fingerX = event!!.x
        val fingerY = event.y
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath.moveTo(fingerX, fingerY)          //двигает кисть в заданню точку
                mDrawPath.lineTo(fingerX, fingerY)          //рисует линию из точки, где находится кисть, в заданную точку
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath.lineTo(fingerX, fingerY)
            }
            MotionEvent.ACTION_UP -> {
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint)
                mDrawPath.reset()
            }
        }
        invalidate()
        return true
    }

    fun getBitmap() : Bitmap {
        val tmpBitmap : Bitmap = mCanvasBitmap
        return mCanvasBitmap
    }
}