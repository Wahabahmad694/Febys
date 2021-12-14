package com.hexagram.febys.ui.screens.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentMenuBinding
import com.hexagram.febys.ui.screens.search.SearchFragmentDirections
import com.hexagram.febys.utils.navigateTo

class MenuFragment : BaseFragment() {
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUiListener()
    }

    private fun initUiListener() {
        binding.labelFebysPlus.setOnClickListener {
            val gotofebysPlus =
                MenuFragmentDirections.actionMenuFragmentToFebysPlusFragment()
            navigateTo(gotofebysPlus)
        }
    }
}