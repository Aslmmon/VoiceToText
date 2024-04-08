package com.idmt.simplevoice.ui.network.model.comments_model

data class GetUserCommentsResponseItem(
    val comments: String,
    val sectiondataid: Int,
    val userid: Int
)