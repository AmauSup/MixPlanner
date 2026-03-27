package com.supdevinci.mixplanner.model

data class GlassListResponse(
    val drinks: List<GlassItem>?
)

data class GlassItem(
    val strGlass: String?
)