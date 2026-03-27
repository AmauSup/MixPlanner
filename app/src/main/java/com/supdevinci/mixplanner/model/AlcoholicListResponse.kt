package com.supdevinci.mixplanner.model

data class AlcoholicListResponse(
    val drinks: List<AlcoholicItem>?
)

data class AlcoholicItem(
    val strAlcoholic: String?
)