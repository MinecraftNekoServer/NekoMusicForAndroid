package com.neko.music.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchHistory(
    val query: String,
    val timestamp: Long
)