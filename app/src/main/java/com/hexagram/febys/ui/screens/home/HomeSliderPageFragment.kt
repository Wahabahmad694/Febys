package com.hexagram.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentHomeSliderPageBinding
import com.hexagram.febys.network.response.Banner

private const val ARG_IMAGE = "imageArg"

class HomeSliderPageFragment : com.hexagram.febys.base.BaseFragment() {
    private lateinit var imageUrl: String

    private lateinit var binding: FragmentHomeSliderPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString(ARG_IMAGE)!!

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
    }

    companion object {
        @JvmStatic
        fun newInstance(banner: Banner) =
            HomeSliderPageFragment().apply {
                arguments = Bundle().apply { putString(ARG_IMAGE, banner.images[0]) }
            }
    }
}