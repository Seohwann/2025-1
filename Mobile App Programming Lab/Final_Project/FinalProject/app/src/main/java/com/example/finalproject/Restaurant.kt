package com.example.finalproject
import com.google.gson.annotations.SerializedName

data class Restaurant(
    val id: Int,
    val restaurant: String,
    val type: String,
    val location: String,
    val rating: String,
    val image: String,
    val description: String,
    val openingHours: OpeningHours,
    @SerializedName("Menu") val menu: List<MenuItem>
)

data class OpeningHours(
    val open: String,
    val close: String
)

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Int,
    val image: String
)