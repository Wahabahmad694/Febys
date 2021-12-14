package com.hexagram.febys.ui.screens.febysPlus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentFebysPlusBinding
import com.hexagram.febys.databinding.FragmentFebysPlusPackagesBinding
import com.hexagram.febys.utils.goBack
import dagger.hilt.android.AndroidEntryPoint


class FebysPlusPackages:BaseFragment() {
    private lateinit var binding:FragmentFebysPlusPackagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ):  View {
        binding = FragmentFebysPlusPackagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUiListener()
    }

    private fun initUiListener() {
        binding.ivBack.setOnClickListener { goBack() }
    }
}