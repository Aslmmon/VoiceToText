package com.idmt.simplevoice.ui.InputEntry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idmt.simplevoice.ui.network.RetrofitMoviesNetworkApi
import com.idmt.simplevoice.ui.network.model.category_response.CategoryDropDownResponse
import com.idmt.simplevoice.ui.network.model.zone_response.ZonDropDownResponse
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


    val _zoneDistrict: MutableStateFlow<MutableList<Pair<String, Int>>> =
        MutableStateFlow(mutableListOf())

    val zoneDistrict: MutableStateFlow<MutableList<Pair<String, Int>>> get() = _zoneDistrict


    val _zoneStations: MutableStateFlow<MutableList<Pair<String, Int>>> =
        MutableStateFlow(mutableListOf())

    val zoneStations: MutableStateFlow<MutableList<Pair<String, Int>>> get() = _zoneStations


    val _zones: MutableStateFlow<MutableList<Pair<String, Int>>> =
        MutableStateFlow(mutableListOf())
    val zones: MutableStateFlow<MutableList<Pair<String, Int>>> get() = _zones

    var categoryDropDownResponse = CategoryDropDownResponse()
    var zonesDropDownResponse = ZonDropDownResponse()

    val categoryId = mutableStateOf(0)
    val subCategoryId = mutableStateOf(0)
    val zoneId = mutableStateOf(0)
    val districtId = mutableStateOf(0)
    val stationId = mutableStateOf(0)
    val date = mutableStateOf("")
    val inputData = mutableStateOf("")

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

    fun getZones() {
        viewModelScope.launch {
            zonesDropDownResponse = retrofitMoviesNetworkApi.getZoneDropDown()
            val zonePairs = mutableListOf(Pair("", 0))
            zonesDropDownResponse.forEachIndexed { index, zone ->
                zonePairs.add(Pair(zone.zone, zone.zoneId))
            }
            _zones.update {
                zonePairs
            }
        }

    }

    fun showLoading() {
        _categoryState.update {
            UiState.Loading()
        }
    }

    fun updateDate(text: String) {
        date.value = text
    }


    fun submitInputEntry() {
        viewModelScope.launch {
            try {
                retrofitMoviesNetworkApi.submitInputEntry(
                    date = date.value,
                    categoryId = categoryId.value,
                    subCategoryId = subCategoryId.value,
                    zoneId = zoneId.value,
                    stationId = stationId.value,
                    inputData = inputData.value
                )
            } catch (e: Exception) {

            }
        }
    }

    fun updateSubCategories(id: Int) {
        categoryId.value = id
        val subCategoryPairs = mutableListOf(Pair("", 0))
        categoryDropDownResponse.find { it.categoryId == id }?.subCategory?.forEachIndexed { index, subCategory ->
            subCategoryPairs.add(Pair(subCategory.subCategoryName, subCategory.subCategoryId))
        }
        _subCategories.update {
            subCategoryPairs
        }
    }

    fun selectSubCategory(id: Int) {
        subCategoryId.value = id
    }

    /**
     * zone
     */
    fun updateZoneDistricts(id: Int) {
        zoneId.value = id
        val zoneDistricts = mutableListOf(Pair("", 0))
        zonesDropDownResponse.find { it.zoneId == id }?.district?.forEachIndexed { index, district ->
            zoneDistricts.add(Pair(district.district, district.districtId))
        }
        zoneDistrict.update {
            zoneDistricts
        }
    }

    /**
     * districtId
     */

    fun updateZoneStations(id: Int) {
        districtId.value = id
        val zoneStations = mutableListOf(Pair("", 0))
        zonesDropDownResponse.map {
            val destrict = it.district.find { it.districtId == id }
            destrict?.station?.forEachIndexed { index, station ->
                zoneStations.add(Pair(station.stationName, station.stationId))
            }
        }

        _zoneStations.update {
            zoneStations
        }
    }

    fun selectZoneStation(id: Int) {
        stationId.value = id
    }

    fun updateInputData(text: String) {
        inputData.value = text
    }

    sealed class UiState {
        data class Success(val data: MutableList<Pair<String, Int>>) : UiState()
        data class Error(val message: String) : UiState()
        class Loading : UiState()

    }

}