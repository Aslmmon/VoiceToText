package com.idmt.simplevoice.ui.network.model.zone_response

data class District(
    val district: String,
    val districtId: Int,
    val station: List<Station>
)