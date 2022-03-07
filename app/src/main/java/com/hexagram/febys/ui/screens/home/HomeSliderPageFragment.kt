package com.hexagram.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentHomeSliderPageBinding
import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.network.response.Offer
import com.hexagram.febys.utils.navigateTo

private const val ARG_IMAGE = "imageArg"
private const val ARG_CATEGORY_NAME = "categoryName"
private const val ARG_CATEGORY_ID = "categoryId"

class HomeSliderPageFragment : BaseFragment() {
    private lateinit var imageUrl: String
    private var categoryName: String? = null
    private var categoryId: Int = 0

    private lateinit var binding: FragmentHomeSliderPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString(ARG_IMAGE)!!
        categoryName = requireArguments().getString(ARG_CATEGORY_NAME)
        categoryId = requireArguments().getInt(ARG_CATEGORY_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeSliderPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageUrl = imageUrl
        binding.root.setOnClickListener {
            if (categoryName != null && categoryId != 0) {
                val gotoCategoryListing = HomeFragmentDirections
                    .actionHomeFragmentToCategoryProductListingFragment(categoryName!!, categoryId)
                navigateTo(gotoCategoryListing)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(banner: Banner) =
            HomeSliderPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, banner.image[0])
                    putString(ARG_CATEGORY_NAME, banner.name)
                    putInt(ARG_CATEGORY_ID, banner.categoryId)
                }
            }

        @JvmStatic
        fun newInstance(categoryName: String, offer: Offer) =
            HomeSliderPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, offer.images[0])
                    putString(ARG_CATEGORY_NAME, categoryName)
                    putInt(ARG_CATEGORY_ID, offer.categoryId)
                }
            }
    }
}