package com.idmt.simplevoice.ui.InputEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idmt.simplevoice.ui.network.RetrofitMoviesNetworkApi
import com.idmt.simplevoice.ui.network.model.category_response.CategoryDropDownResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InputViewModel : ViewModel() {
    private var retrofitMoviesNetworkApi = RetrofitMoviesNetworkApi()

    private var _categoryState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState.Loading()
    )
    val categoryState: MutableStateFlow<UiState> get() = _categoryState


    val _subCategories: MutableStateFlow<MutableList<Pair<String, Int>>> =
        MutableStateFlow(mutableListOf())

    val subCategories: MutableStateFlow<MutableList<Pair<String, Int>>> get() = _subCategories

    var categoryDropDownResponse = CategoryDropDownResponse()


    init {
        getCategories()
    }

    fun getCategories() {

        viewModelScope.launch {
            try {
                categoryDropDownResponse = retrofitMoviesNetworkApi.getCategoryDropDown()
                val categoryPairs = mutableListOf(Pair("", 0))
                categoryDropDownResponse.forEachIndexed { index, category ->
                    categoryPairs.add(Pair(category.categoryName, category.categoryId))
                }
                _categoryState.update {
                    UiState.Success(categoryPairs)
                }
            } catch (e: Exception) {
                _categoryState.update {
                    UiState.Error(e.message.toString())
                }
            }
        }

    }

    fun showLoading() {
        _categoryState.update {
            UiState.Loading()
        }
    }

    fun updateSubCategories(id: Int) {
        val subCategoryPairs = mutableListOf(Pair("", 0))
        categoryDropDownResponse.find { it.categoryId == id }?.subCategory?.forEachIndexed { index, subCategory ->
            subCategoryPairs.add(Pair(subCategory.subCategoryName, subCategory.subCategoryId))
        }
        _subCategories.update {
            subCategoryPairs
        }
    }


    sealed class UiState {
        data class Success(val data: MutableList<Pair<String, Int>>) : UiState()
        data class Error(val message: String) : UiState()
        class Loading : UiState()

    }

}