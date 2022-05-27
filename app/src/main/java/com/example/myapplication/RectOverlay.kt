package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class RectOverlay {
    class RectOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
        View(context, attributeSet) {

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val iter = ImageAnalyzer.blocks.iterator();
            val sr = ImageAnalyzer.text.iterator();
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.FILL

            while (iter.hasNext()){
                val rect=iter.next()
                canvas.drawText(sr.next(), rect.top.toFloat(), rect.left.toFloat(), paint)
            }
            invalidate()
        }
    }
}