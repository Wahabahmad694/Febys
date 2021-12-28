package com.hexagram.febys.ui.screens.order.detail

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class IBindViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(position: Int)
}