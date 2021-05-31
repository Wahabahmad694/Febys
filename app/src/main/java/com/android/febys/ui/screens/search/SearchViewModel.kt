package com.android.febys.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.febys.repos.SearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: SearchRepo
) : ViewModel() {
    fun fetchAllCategories() = repo.fetchAllCategories(viewModelScope)

    fun fetchTabs() = repo.fetchTabs()
}