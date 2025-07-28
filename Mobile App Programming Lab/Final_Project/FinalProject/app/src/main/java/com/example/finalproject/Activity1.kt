package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Reservation
import com.example.finalproject.Restaurant
import com.example.finalproject.User
import com.example.finalproject.JsonUtils

class Activity1 : AppCompatActivity() {
    private lateinit var users: List<User>
    private lateinit var restaurants: List<Restaurant>
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)

        // JSON 데이터 로드
        users = JsonUtils.loadUsers(this)
        restaurants = JsonUtils.loadRestaurants(this)

        // 넘겨받은 intent를 통해 현재 로그인 된 userinfo 가져오기
        val userId = intent.getStringExtra("USER_ID") ?: return
        currentUser = users.find { it.id == userId } ?: return

        // userinfo 표시
        val userInfoText = findViewById<TextView>(R.id.userInfoText)
        userInfoText.text = "${currentUser.id}: ${currentUser.info.name} (${currentUser.info.age}/${currentUser.info.gender})"

        // 현재 user의 예약 목록을 adapter를 이용하여 Listview에 넣어줌으로써 구현
        val reservationList = findViewById<ListView>(R.id.reservationList)
        val adapter = ReservationAdapter(this, currentUser.reserved, restaurants)
        reservationList.adapter = adapter

        // 예약 항목 클릭 시 Activity 6 시작
        reservationList.setOnItemClickListener { _, _, position, _ ->
            val reservation = currentUser.reserved[position]
            val intent = Intent(this, Activity6::class.java)
            intent.putExtra("RESERVATION_ID", reservation.reservationId)
            intent.putExtra("USER_ID", currentUser.id)
            startActivity(intent)
        }

        // Reservation 버튼 클릭 시 Activity 2 시작
        val reservationButton = findViewById<Button>(R.id.reservationButton)
        reservationButton.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            intent.putExtra("USER_ID", currentUser.id)
            startActivity(intent)
        }

        // back 버튼을 누르면 앱이 종료되게 끔 설정
        onBackPressedDispatcher.addCallback(this) {
            finishAffinity()
        }

    }

    private inner class ReservationAdapter(
        private val context: Context,
        private val reservations: List<Reservation>,
        private val restaurants: List<Restaurant>
    ) : BaseAdapter() {
        override fun getCount(): Int = reservations.size

        override fun getItem(position: Int): Any = reservations[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.reservation_list_item, parent, false)
            val reservation = reservations[position]
            val restaurant = restaurants.find { it.id == reservation.restaurantId }

            val thumbnail = view.findViewById<ImageView>(R.id.reservationThumbnail)
            val resName = view.findViewById<TextView>(R.id.reservationRestaurantName)
            val people = view.findViewById<TextView>(R.id.reservationPeople)
            val dateTime = view.findViewById<TextView>(R.id.reservationDateTime)
            
            // 어댑터 내부의 getView함수에서 Listview에 들어갈 각 item에 대한 정보를 View에 넣어줌
            thumbnail.setImageResource(
                resources.getIdentifier(restaurant?.image ?: "default_thumbnail", "drawable", packageName)
            )
            resName.text = restaurant?.restaurant
            people.text = "People: ${reservation.numberOfPeople}"
            dateTime.text = "${reservation.time} ${reservation.date}"

            return view
        }
    }
}