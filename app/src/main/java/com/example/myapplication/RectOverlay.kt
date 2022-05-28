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
            val paint = Paint()
            paint.color = Color.WHITE
            paint.style = Paint.Style.FILL
            paint.textSize=60f;
            val bl = MyTranslator.blocks
            val tx = MyTranslator.texts
            val iter = bl.iterator()
            val sr = tx.iterator()
            while (iter.hasNext()&&sr.hasNext()){
                val rect=iter.next()
                canvas.drawText(sr.next(), rect.left.toFloat()*3f-canvas.width/6, rect.top.toFloat()*3f, paint)
            }
            invalidate()
        }
    }
}