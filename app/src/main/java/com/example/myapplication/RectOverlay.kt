package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.myapplication.MyTranslator

class RectOverlay {
    class RectOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
        View(context, attributeSet) {

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val iter = MyTranslator.blocks.iterator();
            val sr = MyTranslator.texts.iterator();
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.FILL
            paint.textSize=60f;

            while (iter.hasNext()&&sr.hasNext()){
                val rect=iter.next()
                canvas.drawText(sr.next(), rect.top.toFloat(), rect.left.toFloat(), paint)
            }
            invalidate()
        }
    }
}