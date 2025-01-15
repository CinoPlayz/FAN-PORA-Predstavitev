package com.example.farmcontractor.Structs

import kotlinx.serialization.Serializable

@Serializable
data class PostContractRequest(val work: String, val contractor: String, val lat: Double, val lng: Double)
