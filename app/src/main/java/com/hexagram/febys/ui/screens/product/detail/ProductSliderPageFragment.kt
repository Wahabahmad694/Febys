package com.hexagram.febys.ui.screens.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentProductSliderPageBinding
import com.hexagram.febys.utils.navigateTo

private const val ARG_IMAGE = "imageArg"
private const val ARG_IMAGE_LIST = "IMAGES"

class ProductSliderPageFragment : BaseFragment() {
    private lateinit var imageUrl: String
    private lateinit var images: ArrayList<String>

    private lateinit var binding: FragmentProductSliderPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString(ARG_IMAGE)!!
        images = requireArguments().getStringArrayList(ARG_IMAGE_LIST)!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductSliderPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageUrl = imageUrl
        binding.ivProductImage.setOnClickListener {
            val gotoImagePreview =
                NavGraphDirections.toImagePreviewFragment(image = images.toTypedArray())
            navigateTo(gotoImagePreview)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String, images: List<String>) =
            ProductSliderPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, imageUrl)
                    putStringArrayList("IMAGES", images as ArrayList<String>)
                }
            }
    }
}