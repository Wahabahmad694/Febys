package com.hexagram.febys.ui.screens.febysPlus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemsFebysPlusBinding
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.utils.load

class FebysPlusAdapter(private val context: Context) :
    RecyclerView.Adapter<FebysPlusAdapter.ViewHolder>() {
    private var packages = listOf<Package>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsFebysPlusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return packages.size
    }

    fun submitList(febysPackage: List<Package>) {
        this.packages = febysPackage
        notifyItemRangeChanged(0, packages.size)
    }

    inner class ViewHolder(
        private val binding: ItemsFebysPlusBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val febysPackage = packages[position]
            ivFebysPlus.load(febysPackage.icon)
            tvTitle.text = febysPackage.title
            tvDays.text = febysPackage.subscriptionDays.toString().plus(" Days")
            tvPrice.text = febysPackage.price.getFormattedPrice()
            tvFeature.text = febysPackage.features.joinToString(",") { it.title }

            if (febysPackage.selected) {
                // show selected ui
                markAsSelected(binding.containerBasicFreeDelivery)
//                ContextCompat.getDrawable(context, R.drawable.bg_border_dark_red)
            } else {
                //show unslected ui
                markAsNotSelected(binding.containerBasicFreeDelivery)
//                ContextCompat.getDrawable(context, R.drawable.bg_border_dark_grey)
            }

            containerBasicFreeDelivery.setOnClickListener {
                packages.map {
                    it.selected = false
                    it
                }
                packages[position].selected = true
                notifyDataSetChanged()
            }
        }
    }

     fun markAsNotSelected(backgroundView: View) {
                backgroundView.background =
            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_grey)
    }
     fun markAsSelected(backgroundView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_red)
    }
//
//    private fun updateUi(selectedBackground: View) {
//        markAsSelected(selectedBackground)
//        markAsNotSelected(selectedBackground)
//
//    }
//
//    fun markAsSelected(backgroundView: View) {
//        backgroundView.background =
//            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_red)
//    }
//
//    fun markAsNotSelected(backgroundView: View) {
//        backgroundView.background =
//            ContextCompat.getDrawable(backgroundView.context, R.drawable.bg_border_dark_grey)
//    }

}