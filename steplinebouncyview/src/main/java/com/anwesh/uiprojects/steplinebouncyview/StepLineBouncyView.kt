package com.anwesh.uiprojects.steplinebouncyview

/**
 * Created by anweshmishra on 24/12/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
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
