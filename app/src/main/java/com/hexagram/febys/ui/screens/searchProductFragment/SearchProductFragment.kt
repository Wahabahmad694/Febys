package com.hexagram.febys.ui.screens.searchProductFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentSearchProductBinding
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideKeyboard
import com.hexagram.febys.utils.onSearch

class SearchProductFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiListener()
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener {
            goBack()
        }
        fun onSearchClick() {
            hideKeyboard()
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
//                doSearch(query)
            }
        }
        binding.ivClear.setOnClickListener { binding.etSearch.setText("") }
        binding.ivSearch.setOnClickListener { onSearchClick() }

        binding.etSearch.onSearch { onSearchClick() }

        binding.etSearch.addTextChangedListener {
            binding.ivClear.isVisible = binding.etSearch.text.isNotEmpty()
        }

//        private fun doSearch(query: String) {
//            val gotoSearch =
//                SearchFragmentDirections.actionSearchFragmentToSearchProductListingFragment(
//                    query
//                )
//            navigateTo(gotoSearch)
//        }
    }

}