package com.example.farmcontractor.Structs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("_id")
    val id: MongoId = MongoId(),
    val username: String = "",
    val password: String = "",
    val typeOfUser: String = "",
    val token: String = ""
)
