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
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.STROKE
            if(ImageAnalyzer.blocks.size>0){
                canvas.drawCircle(100f,100f,20f,paint)
            }
            while (iter.hasNext()){
                canvas.drawRect(iter.next(), paint)
            }
            invalidate()
        }
    }
}