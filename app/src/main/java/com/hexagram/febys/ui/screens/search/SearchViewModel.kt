package com.hexagram.febys.ui.screens.search

import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.repos.ISearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: ISearchRepo
) : BaseViewModel() {

    val allCategoryPagingData = repo.fetchAllCategories(viewModelScope)
}