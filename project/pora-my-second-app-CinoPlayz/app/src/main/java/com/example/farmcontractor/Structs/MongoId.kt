package com.example.farmcontractor.Structs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MongoId(
    @SerialName("\$oid")
    val oid: String = ""
)
