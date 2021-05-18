package com.sid.app.verbpractice.helper

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class SwipelessDrawerLayout : DrawerLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val isSwipeOpenEnabled: Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeOpenEnabled && !isDrawerVisible(GravityCompat.START)) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeOpenEnabled && !isDrawerVisible(GravityCompat.START)) {
            return false
        }
        return super.onTouchEvent(ev)
    }
}