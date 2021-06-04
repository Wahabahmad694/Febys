package com.android.febys.ui.screens.productDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.febys.databinding.FragmentProductSliderPageBinding

private const val ARG_IMAGE = "param1"

class ProductSliderPageFragment : Fragment() {
    private lateinit var imageUrl: String

    private lateinit var binding: FragmentProductSliderPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString(ARG_IMAGE)!!

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
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String) =
            ProductSliderPageFragment().apply {
                arguments = Bundle().apply { putString(ARG_IMAGE, imageUrl) }
            }
    }
}