package com.supdevinci.mixplanner.model

data class CategoryListResponse(
    val drinks: List<CategoryItem>?
)

data class CategoryItem(
    val strCategory: String?
)