package com.app.matchinggame

data class MemoryCard(
    val identifier: Int,
    val imageResId: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
