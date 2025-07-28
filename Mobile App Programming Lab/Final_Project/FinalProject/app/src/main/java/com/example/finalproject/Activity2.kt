package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Reservation
import com.example.finalproject.Restaurant
import com.example.finalproject.User
import com.example.finalproject.JsonUtils

class Activity2 : AppCompatActivity() {
    private lateinit var restaurants: List<Restaurant>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)


        // JSON 데이터 로드
        val users = JsonUtils.loadUsers(this)
        restaurants = JsonUtils.loadRestaurants(this)
        
        val userId = intent.getStringExtra("USER_ID") ?: return
        val user = users.find { it.id == userId } ?: return

        // 각각의 레스토랑을 어댑터로 구현해서 레스토랑 Listview에 표현
        val restaurantList = findViewById<ListView>(R.id.restaurantList)
        val adapter = RestaurantAdapter(this, restaurants)
        restaurantList.adapter = adapter
        
        // 레스토랑 각각을 누르면 해당 레스토랑에 대한 정보가 Activity 3에서 나오도록 구현하고 해당 레스토랑의 ID를 Activity 3으로 넘겨줌 
        restaurantList.setOnItemClickListener { _, _, position, _ ->
            val restaurant = restaurants[position]
            val intent = Intent(this, Activity3::class.java)
            intent.putExtra("RESTAURANT_ID", restaurant.id)
            intent.putExtra("USER_ID", user.id)
            startActivity(intent)
        }

        // back 버튼을 누르면 앱이 바로 전 Activity로 돌아가게 끔 설정
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    private inner class RestaurantAdapter(
        private val context: Context,
        private val restaurantList: List<Restaurant>
    ) : BaseAdapter() {
        override fun getCount(): Int = restaurantList.size

        override fun getItem(position: Int): Any = restaurantList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.restaurant_list_item, parent, false)
            val restaurant = restaurantList[position]

            val restaurantImage = view.findViewById<ImageView>(R.id.restaurantImage)
            val nameText = view.findViewById<TextView>(R.id.restaurantNameText)
            val locationRatingText = view.findViewById<TextView>(R.id.locationRatingText)
            val hoursText = view.findViewById<TextView>(R.id.openingHoursText)

            restaurantImage.setImageResource(
                resources.getIdentifier(restaurant.image, "drawable", packageName)
            )

            nameText.text = restaurant.restaurant
            locationRatingText.text = "${restaurant.location} / ${restaurant.rating}"
            hoursText.text = "${restaurant.openingHours.open} ~ ${restaurant.openingHours.close}"

            return view
        }
    }
}