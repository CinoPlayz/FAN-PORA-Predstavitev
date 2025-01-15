package com.example.farmcontractor.Structs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Work(
    @SerialName("_id")
    val id: MongoId = MongoId(),
    val username: String = "",
    val work: String = ""
)
