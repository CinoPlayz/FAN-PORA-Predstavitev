package com.example.farmcontractor.Structs

import kotlinx.serialization.Serializable

@Serializable
data class PutWorkRequest(val work: String, val workOld: String)
