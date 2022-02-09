package com.hexagram.febys.ui.screens.febysPlus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemsFebysPlusBinding
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.utils.load

class FebysPlusAdapter() :
    RecyclerView.Adapter<FebysPlusAdapter.ViewHolder>() {
    private var packages = listOf<Package>()
    private var purchasedPkgId: String? = null

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

    var onItemClick: ((packages: Package) -> Unit)? = null

    fun submitList(febysPackage: List<Package>, purchasedPkgId: String? = null) {
        this.packages = febysPackage
        this.purchasedPkgId = purchasedPkgId
        packages.forEach { it.selected = purchasedPkgId == it.id }
        notifyItemRangeChanged(0, packages.size)
    }

    fun getSelectedPkg() = packages.firstOrNull { it.selected }

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
                markAsSelected(binding.containerBasicFreeDelivery)
            } else {
                markAsNotSelected(binding.containerBasicFreeDelivery)
            }

            if (purchasedPkgId.isNullOrEmpty()) {
                root.setOnClickListener {
                    onItemClick?.invoke(febysPackage)
                    packages.forEach { it.selected = false }
                    packages[position].selected = true
                    notifyDataSetChanged()
                }
            } else {
                overlay.isVisible = febysPackage.id != purchasedPkgId
                icStatus.isVisible = febysPackage.id == purchasedPkgId
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
}