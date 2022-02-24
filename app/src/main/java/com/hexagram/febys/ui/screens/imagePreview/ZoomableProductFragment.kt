package com.hexagram.febys.ui.screens.imagePreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentZoomableProductBinding

private const val ARG_IMAGE = "imageArg"
private const val ARG_IMAGE_LIST = "IMAGES"

class ZoomableProductFragment : BaseFragment() {
    private lateinit var imageUrl: String
    private lateinit var images: ArrayList<String>

    private lateinit var binding: FragmentZoomableProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString(ARG_IMAGE)!!
        images = requireArguments().getStringArrayList(ARG_IMAGE_LIST)!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentZoomableProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(imageUrl).into(binding.ivProductImage)
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String, images: List<String>) =
            ZoomableProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, imageUrl)
                    putStringArrayList("IMAGES", images as ArrayList<String>)
                }
            }
    }
}
