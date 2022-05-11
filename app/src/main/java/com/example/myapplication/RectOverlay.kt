package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
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
            val paint = Paint()
            while (iter.hasNext()){
                canvas.drawRect(iter.next(), paint)
            }
        }
    }
}