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
    val sc : Float = scale.divideScale(lines + i, 2 * lines).cosify()
    val y : Float = h * offset + (yUp) * sf
    save()
    translate(i * xGap, y)
    drawLine(0f, 0f, xGap, 0f, paint)
    restore()
    drawRect(RectF(0f, finalY - yUp * sc, xGap, finalY), paint)
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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SLBNode(var i : Int, val state : State = State()) {

        private var next : SLBNode? = null
        private var prev : SLBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SLBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSLBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SLBNode {
            var curr : SLBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class StepLineBouncy(var i : Int) {

        private val root : SLBNode = SLBNode(0)
        private var curr : SLBNode = root
        private var dir : Int = 1

        fun  draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : StepLineBouncyView) {

        private val animator : Animator = Animator(view)
        private val slb : StepLineBouncy = StepLineBouncy(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            slb.draw(canvas, paint)
            animator.animate {
                slb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            slb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : StepLineBouncyView {
            val view : StepLineBouncyView = StepLineBouncyView(activity)
            activity.setContentView(view)
            return view
        }
    }
}