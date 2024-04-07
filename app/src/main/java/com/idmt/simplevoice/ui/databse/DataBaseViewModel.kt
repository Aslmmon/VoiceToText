package com.idmt.simplevoice.ui.databse

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idmt.simplevoice.ui.network.RetrofitMoviesNetworkApi
import com.idmt.simplevoice.ui.network.model.ListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DataBaseViewModel : ViewModel() {

    private var retrofitMoviesNetworkApi = RetrofitMoviesNetworkApi()

    private var _listState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading())
    val listState: MutableStateFlow<UiState> get() = _listState

    private var _SectionStates: MutableStateFlow<SECIONS> = MutableStateFlow(SECIONS.Political)
    val SectionStates: MutableStateFlow<SECIONS> get() = _SectionStates


    private val _Comments: MutableStateFlow<MutableList<String>> = MutableStateFlow(mutableListOf())
    val Comments: StateFlow<MutableList<String>> = _Comments.asStateFlow()

    val selectedSection: MutableState<SECIONS?> = mutableStateOf(null)


    suspend fun getList(roleid: Int, sectionId: Int) {

        viewModelScope.launch {
            try {
                var list = retrofitMoviesNetworkApi.getList(roleid, sectionId)
                _listState.update {
                    UiState.Success(list)
                }
            } catch (e: Exception) {
                _listState.update {
                    UiState.Error(e.message.toString())
                }
            }
        }

    }

    fun showLoading() {
        _listState.update {
            UiState.Loading()
        }
    }

    fun updateComment(text: String) {
        _Comments.value.add(text)
    }


    fun updateSections(section: SECIONS) {
        _SectionStates.update {
            section
        }
    }


    sealed class UiState {
        data class Success(val data: ListResponse) : UiState()
        data class Error(val message: String) : UiState()
        class Loading : UiState()

    }
}