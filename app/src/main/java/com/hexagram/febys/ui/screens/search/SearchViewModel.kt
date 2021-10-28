package com.hexagram.febys.ui.screens.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.repos.ISearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    searchRepo: ISearchRepo
) : BaseViewModel() {

    val allCategoryPagingData: Flow<PagingData<Category>> = searchRepo.fetchAllCategories(viewModelScope)
}