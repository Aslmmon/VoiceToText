package com.idmt.simplevoice.ui.network.model.zone_response

data class ZonDropDownResponseItem(
    val district: List<District>,
    val zone: String,
    val zoneId: Int
)