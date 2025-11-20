package com.example.hydrationtime.ui.fragments.tips

data class HydrationTip(
    val imageRes: Int,
    val title: String,
    val description: String,
    val url: String? = null // [ADDED] URL field for articles/videos
)