package com.hexagram.febys.ui.screens.febysPlus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFebysPlusBinding
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.navigateTo

class FebysPlusFragment : BaseFragment() {
    private lateinit var binding: FragmentFebysPlusBinding

    companion object {
        private const val KEY_IS_FEBYS_PLUS = "keyISFebysPlus"

        @JvmStatic
        fun newInstance(isFebysPlus: Boolean = false) = FebysPlusFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_IS_FEBYS_PLUS, isFebysPlus)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFebysPlusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiListener()
    }

    private fun uiListener() {
        binding.btnShow.setOnClickListener {
            val goToFebysExplore =
                FebysPlusFragmentDirections.actionFebysPlusFragmentToFebysPlusFragmentPackages()
            navigateTo(goToFebysExplore)
        }
        binding.ivBack.setOnClickListener { goBack() }
    }
}
