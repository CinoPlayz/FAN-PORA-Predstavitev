package com.example.farmcontractor.constants

class Constants {

    companion object {
        private val work = arrayOf(
            "Bailing",
            "Wrapping",
            "Mowing",
            "Tedding",
            "Rowing",
            "Harrowing",
            "Seeding",
            "Cultivating"
        )
        val url = "http://localhost:8080"

        fun getAllWork(): Array<String> {
            return work
        }
    }
}