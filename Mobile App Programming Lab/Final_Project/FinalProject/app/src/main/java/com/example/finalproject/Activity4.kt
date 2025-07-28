package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Activity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_4)

        val peopleField = findViewById<EditText>(R.id.peopleField)
        val dateField = findViewById<EditText>(R.id.dateField)
        val confirmButton = findViewById<Button>(R.id.confirmButton4)
        val restaurantId = intent.getIntExtra("RESTAURANT_ID",0)

        // 유저 JSON 데이터 로드
        val users = JsonUtils.loadUsers(this)
        val userId = intent.getStringExtra("USER_ID") ?: return
        val user = users.find { it.id == userId } ?: return

        // 기본적으로 confirm 버튼을 누르면 Activity 5를 실행하도록 구현 
        confirmButton.setOnClickListener {
            val people = peopleField.text.toString().toIntOrNull()
            val date = dateField.text.toString()
            
            // poeple field가 1 - 10사이의 값인지 체크 -> invalid하면 다시 setOnclickListener로 돌아감
            if (people == null || people < 1 || people > 10) {
                Toast.makeText(this, "Number of people must be in range from 1 to 10", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Date()
            // 만약 입력받은 Date field의 값이 개발자가 원하는 형식(edittext에 hint로 주어짐)으로 입력받도록 함 -> invalid하면 다시 setOnclickListener로 돌아감
            val inputDate = try {
                dateFormat.parse(date)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format (YYYY-MM-dd)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputDate.before(currentDate)) {
                Toast.makeText(this, "Date must be after today", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, Activity5::class.java)
            intent.putExtra("RESTAURANT_ID", restaurantId)
            intent.putExtra("NUMBER_OF_PEOPLE", people)
            intent.putExtra("DATE", date)
            intent.putExtra("USER_ID", user.id)
            startActivity(intent)
        }

        // back 버튼을 누르면 앱이 바로 전 Activity로 돌아가게 끔 설정
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }
}