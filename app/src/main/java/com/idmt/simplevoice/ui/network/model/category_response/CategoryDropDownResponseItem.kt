package com.idmt.simplevoice.ui.network.model.category_response

data class CategoryDropDownResponseItem(
    val categoryId: Int,
    val categoryName: String,
    val inputTypeId: Int,
    val subCategory: List<SubCategory>
)