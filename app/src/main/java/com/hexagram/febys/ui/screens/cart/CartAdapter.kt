package com.hexagram.febys.ui.screens.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hexagram.febys.R
import com.hexagram.febys.databinding.ItemCartBinding
import com.hexagram.febys.models.db.CartDTO

class CartAdapter(private val isInCheckout: Boolean = false) :
    ListAdapter<CartDTO, CartAdapter.CartViewHolder>(DIFF_UTIL) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<CartDTO>() {
            override fun areItemsTheSame(oldItem: CartDTO, newItem: CartDTO): Boolean {
                return oldItem.skuId == newItem.skuId
            }

            override fun areContentsTheSame(oldItem: CartDTO, newItem: CartDTO): Boolean {
                return oldItem.skuId == newItem.skuId
                        && oldItem.quantity == newItem.quantity
                        && oldItem.hasPromotion == newItem.hasPromotion
                        && oldItem.price.value == newItem.price.value
            }
        }
    }


    private var fav = mutableSetOf<String>()
    var interaction: Interaction? = null
    var showHeader = false

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartDTO: CartDTO) {
            binding.apply {
                cart = cartDTO
                isInCheckout = this@CartAdapter.isInCheckout

                containerVendorDetail.isVisible =
                    showHeader || !isVendorAlreadyDisplayed(cartDTO)
                showHeader = false

                ivAdd.setOnClickListener {
                    val updatedCartItem = cartDTO.copy(quantity = cartDTO.quantity + 1)
                    interaction?.updateCartItem(updatedCartItem)
                }

                ivMinus.setOnClickListener {
                    if (cartDTO.quantity == 1) {
                        removeCartItemAndNotifyNext(cartDTO)
                    } else {
                        val updatedCartItem = cartDTO.copy(quantity = cartDTO.quantity - 1)
                        interaction?.updateCartItem(updatedCartItem)
                    }
                }

                ivRemove.setOnClickListener {
                    removeCartItemAndNotifyNext(cartDTO)
                }

                ivProduct.setOnClickListener {
                    interaction?.openProductDetail(cartDTO)
                }

                val isFav = cartDTO.skuId in fav
                if (isFav) {
                    ivFavToggle.setImageResource(R.drawable.ic_fav)
                    tvFav.setText(R.string.label_remove_from_wishlist)
                } else {
                    ivFavToggle.setImageResource(R.drawable.ic_un_fav)
                    tvFav.setText(R.string.label_add_to_wishlist)
                }

                ivFavToggle.setOnClickListener {
                    toggleFav(cartDTO)
                }

                binding.tvFav.setOnClickListener {
                    toggleFav(cartDTO)
                }
            }
            binding.executePendingBindings()
        }

        private fun toggleFav(cartDTO: CartDTO) {
            val isUserLoggedIn =
                interaction?.toggleFavIfUserLoggedIn(cartDTO.skuId) ?: false
            if (!isUserLoggedIn) return

            if (cartDTO.skuId in fav) {
                fav.remove(cartDTO.skuId)
            } else {
                fav.add(cartDTO.skuId)
            }
            val position = currentList.indexOf(cartDTO)
            notifyItemChanged(position)
        }

        private fun removeCartItemAndNotifyNext(cartDTO: CartDTO) {
            interaction?.removeFromCart(cartDTO)
            val position = currentList.indexOf(cartDTO)
            if (position == currentList.size - 1) return

            val currentVendorId = getItem(position).vendorId
            val nextVendorId = getItem(+1).vendorId
            val previousVendorId = if (position > 0) getItem(position - 1).vendorId else -1

            showHeader = currentVendorId != previousVendorId && currentVendorId == nextVendorId

            if (showHeader) notifyItemChanged(position + 1)
        }

        private fun isVendorAlreadyDisplayed(cartDTO: CartDTO): Boolean {
            val position = currentList.indexOf(cartDTO)
            return cartDTO.vendorId in currentList.subList(0, position).map { it.vendorId }
        }

    }

    override fun submitList(list: List<CartDTO>?) {
        super.submitList(list)
    }

    fun submitFav(fav: MutableSet<String>) {
        this.fav = fav
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Interaction {
        fun updateCartItem(cartDTO: CartDTO)
        fun toggleFavIfUserLoggedIn(skuId: String): Boolean
        fun removeFromCart(cartDTO: CartDTO)
        fun openProductDetail(cartDTO: CartDTO)
    }
}