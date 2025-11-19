package com.spotitworld.data

data class HuntItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val funFact: String,
    val isFound: Boolean = false
)
