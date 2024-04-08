package com.idmt.simplevoice.ui.databse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idmt.simplevoice.ui.databse.data.SECIONS
import com.idmt.simplevoice.ui.network.RetrofitMoviesNetworkApi
import com.idmt.simplevoice.ui.network.model.ListResponse
import com.idmt.simplevoice.ui.network.model.comments_model.CommentsSuccessResponse
import com.idmt.simplevoice.ui.network.model.comments_model.GetUserCommentsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DataBaseViewModel : ViewModel() {

    private var retrofitMoviesNetworkApi = RetrofitMoviesNetworkApi()

    private var _listState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading())
    val listState: MutableStateFlow<UiState> get() = _listState

    private var _SectionStates: MutableStateFlow<SECIONS> = MutableStateFlow(SECIONS.Political)
    val SectionStates: MutableStateFlow<SECIONS> get() = _SectionStates


    private var _CommentsState: MutableStateFlow<CommentsUiState?> =
        MutableStateFlow(null)
    val CommentsState: MutableStateFlow<CommentsUiState?> get() = _CommentsState


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


    fun getComments(sectionId: Int) {

        viewModelScope.launch {
            _CommentsState.update {
                CommentsUiState.Loading()
            }
            try {
                val response = retrofitMoviesNetworkApi.getSectionComments(sectionId)
                _CommentsState.update {
                    CommentsUiState.Success(response)
                }
            } catch (e: Exception) {
                _CommentsState.update {
                    CommentsUiState.Error(e.message.toString())
                }
            }
        }

    }


    fun submitComments(sectionId: Int, comment: String, userId: Int) {
        viewModelScope.launch {
            try {
                val response =
                    retrofitMoviesNetworkApi.submitSectionComment(sectionId, comment, userId)
                //  getComments(sectionId)
                _CommentsState.update {
                    CommentsUiState.SubmitSuccess(response)
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

    sealed class CommentsUiState {
        data class Success(val data: GetUserCommentsResponse?) : CommentsUiState()
        data class SubmitSuccess(val commentSuccessResponse: CommentsSuccessResponse) :
            CommentsUiState()

        data class Error(val message: String) : CommentsUiState()
        class Loading : CommentsUiState()

    }
}