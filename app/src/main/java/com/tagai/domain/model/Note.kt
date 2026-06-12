package com.tagai.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val tags: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
