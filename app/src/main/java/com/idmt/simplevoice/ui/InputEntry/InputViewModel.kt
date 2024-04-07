package com.idmt.simplevoice.ui.InputEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idmt.simplevoice.ui.databse.DataBaseViewModel
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


    init {
        getCategories()
    }

    fun getCategories() {

        viewModelScope.launch {
            try {
                var list = retrofitMoviesNetworkApi.getCategoryDropDown()
                _categoryState.update {
                    UiState.Success(list)
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


    sealed class UiState {
        data class Success(val data: CategoryDropDownResponse) : UiState()
        data class Error(val message: String) : UiState()
        class Loading : UiState()

    }

}