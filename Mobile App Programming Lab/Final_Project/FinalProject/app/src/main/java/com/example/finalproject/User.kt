package com.example.finalproject

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val passwd: String,
    val info: UserInfo,
    val reserved: MutableList<Reservation> = mutableListOf()
)

data class UserInfo(
    val name: String,
    val age: Int,
    val gender: String
)

data class Reservation(
    @SerializedName("reservation_id") val reservationId: Int,
    @SerializedName("restaurant_id") val restaurantId: Int,
    @SerializedName("number_of_people") val numberOfPeople: Int,
    val date: String,
    val time: String
)