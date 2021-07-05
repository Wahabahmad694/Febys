package com.android.febys.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.febys.base.BaseFragment
import com.android.febys.databinding.FragmentHomeSeasonalOfferSliderPageBinding
import com.android.febys.network.response.SeasonalOffer

private const val ARG_IMAGE = "imageArg"
private const val ARG_TITLE = "title"

class HomeSeasonalOfferSliderPageFragment : BaseFragment() {
    private lateinit var imageUrl: String
    private lateinit var title: String

    private lateinit var binding: FragmentHomeSeasonalOfferSliderPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = requireArguments().getString(ARG_IMAGE)!!
        title = requireArguments().getString(ARG_TITLE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeSeasonalOfferSliderPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seasonalOfferTitle = title
        binding.imageUrl = imageUrl
    }

    companion object {
        @JvmStatic
        fun newInstance(offer: SeasonalOffer) =
            HomeSeasonalOfferSliderPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, offer.offers[0].images[0])
                    putString(ARG_TITLE, offer.name)
                }
            }
    }
}