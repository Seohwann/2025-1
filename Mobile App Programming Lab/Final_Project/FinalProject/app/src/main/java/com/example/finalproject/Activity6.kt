package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.JsonUtils

class Activity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_6)

        // Activity 1에서 intent를 통해 전송 User와 User의 Reservation 정보를 넘겨받음
        val userId = intent.getStringExtra("USER_ID") ?: return
        val reservationId = intent.getIntExtra("RESERVATION_ID", 0)

        val users = JsonUtils.loadUsers(this)
        val restaurants = JsonUtils.loadRestaurants(this)
        val user = users.find { it.id == userId } ?: return
        val reservation = user.reserved.find { it.reservationId == reservationId } ?: return
        val restaurant = restaurants.find { it.id == reservation.restaurantId } ?: return

        val thumbnail = findViewById<ImageView>(R.id.restaurantThumbnail6)
        val restaurantName = findViewById<TextView>(R.id.restaurantName6)
        val reservationPeople = findViewById<TextView>(R.id.reservationPeople)
        val reservationDates = findViewById<TextView>(R.id.reservationDates)
        val reservationTimes = findViewById<TextView>(R.id.reservationTimes)

        thumbnail.setImageResource(resources.getIdentifier(restaurant.image, "drawable", packageName))
        restaurantName.text = restaurant.restaurant
        reservationPeople.text = "People : ${reservation.numberOfPeople}"
        reservationDates.text = "Dates : ${reservation.date}"
        reservationTimes.text = "Times : ${reservation.time}"

        val cancelButton = findViewById<Button>(R.id.cancelButton6)

        cancelButton.setOnClickListener {
            // cancel 버튼을 누르면 user의 reserved(예약된 목록) 중에서 reservationId가 같은 예약 정보가 있다면 제거한다.
            user.reserved.removeIf { it.reservationId == reservationId }
            JsonUtils.saveUsers(this, users) // 저장
            Toast.makeText(this, "Reservation has been canceled", Toast.LENGTH_SHORT).show() // 예약이 취소되었다는 내용의 토스트 메세지 출력
            val intent = Intent(this, Activity1::class.java) // 다시 Activity 1로 돌아감
            intent.putExtra("USER_ID", user.id)
            startActivity(intent)
            finish()
        }

        // back 버튼을 누르면 앱이 바로 전 Activity로 돌아가게 끔 설정
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }
}