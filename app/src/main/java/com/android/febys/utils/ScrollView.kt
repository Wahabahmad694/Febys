package com.android.febys.utils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.view.isVisible

/**
 * design to support scrollToDescendant(child: View) in below
 */
class ScrollView : ScrollView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var mIsLayoutDirty = true
    private var mChildToScrollTo: View? = null

    override fun scrollToDescendant(child: View) {
        if (!mIsLayoutDirty) {
            val tempRect = Rect()
            child.getDrawingRect(tempRect)

            offsetDescendantRectToMyCoords(child, tempRect)
            val scrollDelta = computeScrollDeltaToGetChildRectOnScreen(tempRect)
            if (scrollDelta != 0) {
                smoothScrollBy(0, scrollDelta)
            }
        } else {
            mChildToScrollTo = child
        }
    }

    override fun requestLayout() {
        mIsLayoutDirty = true
        super.requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mIsLayoutDirty = false
        // Give a child focus if it needs it
        if (mChildToScrollTo != null && isViewDescendantOf(mChildToScrollTo!!, this)) {
            scrollToDescendant(mChildToScrollTo!!)
        }

        mChildToScrollTo = null
    }

    private fun isViewDescendantOf(child: View, parent: View): Boolean {
        if (child === parent) {
            return true
        }
        val theParent = child.parent
        return theParent is ViewGroup && isViewDescendantOf(theParent as View, parent)
    }
}