package com.idmt.simplevoice.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idmt.simplevoice.ui.network.RetrofitMoviesNetworkApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private var retrofitMoviesNetworkApi = RetrofitMoviesNetworkApi()

    private var _homeUiState: MutableStateFlow<HomeUiState?> =
        MutableStateFlow(null)
    val homeUiState: MutableStateFlow<HomeUiState?> get() = _homeUiState


    private var _textToSend: MutableStateFlow<String> = MutableStateFlow("")
    val textSpoken: MutableStateFlow<String> get() = _textToSend


    private var _sectionToSend: MutableStateFlow<Int> = MutableStateFlow(1)
    val sectionChosen: MutableStateFlow<Int> get() = _sectionToSend

    init {
        _textToSend.value=""
    }
    fun submitText() {

        viewModelScope.launch {
            try {
                if (textSpoken.value.isNotEmpty()) {
                    _homeUiState.update {
                        HomeUiState.Loading()
                    }
                    delay(2000)
                    val data = retrofitMoviesNetworkApi.submitText(
                        secitonId = sectionChosen.value,
                        notes = textSpoken.value
                    )
                    _homeUiState.update {
                        HomeUiState.Success(data)
                    }
                }
            } catch (e: Exception) {
                _homeUiState.update {
                    HomeUiState.Error(e.message.toString())
                }
            }
        }


    }

    fun updateText(newText: String) {
        _textToSend.updateAndGet {
            newText
        }
    }


    fun updateSection(newSection: Int) {
        _sectionToSend.getAndUpdate {
            newSection
        }
    }


    sealed class HomeUiState {
        data class Success(val data: Unit) : HomeUiState()
        data class Error(val message: String) : HomeUiState()
        class Loading : HomeUiState()


    }

}