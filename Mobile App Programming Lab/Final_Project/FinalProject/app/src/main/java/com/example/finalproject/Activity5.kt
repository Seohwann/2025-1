package com.example.finalproject

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.addCallback
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Reservation
import com.example.finalproject.JsonUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class Activity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_5)

        val restaurantId = intent.getIntExtra("RESTAURANT_ID", 0)
        val numberOfPeople = intent.getIntExtra("NUMBER_OF_PEOPLE", 0)
        val date = intent.getStringExtra("DATE") ?: ""
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date) ?: throw IllegalArgumentException("Invalid date format")
        val formattedDate = outputFormat.format(parsedDate) // pdf에서 dd/mm/yy 형식으로 나와있기에 이 형식으로 변환해줌

        // 레스토랑 JSON 데이터 로드
        val restaurants = JsonUtils.loadRestaurants(this)
        val restaurant = restaurants.find { it.id == restaurantId } ?: return

        val peopleText = findViewById<TextView>(R.id.people)
        val dateText = findViewById<TextView>(R.id.date)
        val openingHours = findViewById<TextView>(R.id.openinghours)
        val openingHoursText = findViewById<TextView>(R.id.openingHoursText)
        val time = findViewById<TextView>(R.id.time)
        val timeText = findViewById<EditText>(R.id.timeText)
        peopleText.text = "People: $numberOfPeople"
        dateText.text = "Dates: $formattedDate"
        openingHours.text = "Opening hours"
        time.text = "Time"
        openingHoursText.text =
            "[${restaurant.openingHours.open}] ~ [${restaurant.openingHours.close}]"

        val cancelButton = findViewById<Button>(R.id.cancelButton5)
        val confirmButton = findViewById<Button>(R.id.confirmButton5)

        // cancel 버튼을 누르면 Activity 4로 돌아감
        cancelButton.setOnClickListener {
            finish()
        }

        // confirm 버튼을 누르면 Activity 1(예약 현황을 보여주던 화면)으로 돌아감 + Activity1에서 방금 예약한 정보를 반영해줌
        confirmButton.setOnClickListener {
            val timeValue = timeText.text.toString()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val openTime = timeFormat.parse(restaurant.openingHours.open)
            val closeTime = timeFormat.parse(restaurant.openingHours.close)
            val inputTime = try {
                timeFormat.parse(timeValue)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid time format (HH:mm)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // invalid time이기 때문에 activity를 switch하지 않고 토스트 메세지를 출력
            if (inputTime.before(openTime) || inputTime.after(closeTime)) {
                Toast.makeText(this, "Please enter a time within the restaurant's operating hours!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users = JsonUtils.loadUsers(this)
            val userId = intent.getStringExtra("USER_ID") ?: return@setOnClickListener
            val userIndex = users.indexOfFirst { it.id == userId }

            // 새로운 Reservation 객체를 생성함
            val newReservation = Reservation(
                reservationId = users[userIndex].reserved.maxOfOrNull { it.reservationId }?.plus(1) ?: 1,
                restaurantId = restaurantId,
                numberOfPeople = numberOfPeople,
                date = formattedDate,
                time = timeValue
            )
            
            // 내부 저장소에 있는 user_info.json를 읽어와서 해당 userid와 같은 user의 reserved(mutable list)에다가 저장해줌
            users[userIndex].reserved.add(newReservation)
            // 실제 내부 저장소를 업데이트
            JsonUtils.saveUsers(this, users)

            // intent의 필터의 flag를 설정하여 기존에 실행중이던 모든 activity들을 종료시키고, 새로운 task 스택에 Activity 1이 실행되도록 구현
            val intent = Intent(this, Activity1::class.java)
            intent.putExtra("USER_ID", users[userIndex].id)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // back 버튼을 누르면 앱이 바로 전 Activity로 돌아가게 끔 설정
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }
}