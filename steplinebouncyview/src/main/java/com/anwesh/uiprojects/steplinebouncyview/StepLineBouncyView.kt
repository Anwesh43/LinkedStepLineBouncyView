package com.anwesh.uiprojects.steplinebouncyview

/**
 * Created by anweshmishra on 24/12/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color

val nodes : Int = 5
val lines : Int = 4
val strokeFactor : Int = 90
val offset : Float = 0.1f
val scGap : Float = 0.005f
val foreColor : Int = Color.parseColor("#311B92")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 30

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Float.cosify() : Float = Math.cos(this * (Math.PI / 2) + Math.PI / 2).toFloat()

fun Canvas.drawStepLine(i : Int, scale : Float, h : Float, gap : Float, paint : Paint) {
    val xGap : Float = gap / lines
    val finalY : Float = h - offset * h
    val yUp : Float = h - 2 * h * offset
    val sf : Float = scale.sinify().divideScale(i, lines)
    val y : Float = h * offset + (yUp) * sf
    save()
    translate(i * xGap, y)
    drawLine(0f, 0f, xGap, 0f, paint)
    restore()
    drawRect(RectF(0f, finalY - yUp * sf, xGap, finalY), paint)
}

fun Canvas.drawSLBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    paint.color = foreColor
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(gap * (i + 1), 0f)
    drawStepLine(i, scale, h, gap, paint)
    restore()
}

class StepLineBouncyView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}