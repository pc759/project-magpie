package com.spotitworld.data

data class HuntItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val funFact: String,
    val isFound: Boolean = false
)
