package com.example.finalproject

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter

object JsonUtils {
    private val gson = Gson()

    fun loadUsers(context: Context): MutableList<User> {
        val file = File(context.filesDir, "user_info.json")
        if (!file.exists()) {
            // user_info.json이 내부 저장소에 없다면 asset폴더에서 copy해서 사용함
            context.assets.open("user_info.json").use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        // JSON 파일의 전체 텍스트를 읽어온 후 jsonString에 저장
        val jsonString = file.bufferedReader().use { it.readText() }
        val type = object : TypeToken<MutableList<User>>() {}.type
        return gson.fromJson(jsonString, type) ?: mutableListOf()
    }

    fun loadRestaurants(context: Context): List<Restaurant> {
        val jsonString = context.assets.open("restaurant_info.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Restaurant>>() {}.type
        return gson.fromJson(jsonString, type) ?: emptyList()
    }

    fun saveUsers(context: Context, users: List<User>) {
        val jsonString = gson.toJson(users)
        val file = File(context.filesDir, "user_info.json")
        FileWriter(file).use { it.write(jsonString) }
    }
}