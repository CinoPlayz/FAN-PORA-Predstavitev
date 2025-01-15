package com.example.farmcontractor.Structs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contract(
    @SerialName("_id")
    val id: MongoId = MongoId(),
    val work: String = "",
    val contractor: String = "",
    val farmer: String = "",
    val lat: Double,
    val lng: Double,
    var active: Boolean
)
