package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userIdInput = findViewById<EditText>(R.id.userId)
        val passwordInput = findViewById<EditText>(R.id.userpassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        
        //로그인 버튼을 누르면 Activity 1 실행
        loginButton.setOnClickListener {
            val userId = userIdInput.text.toString()
            val password = passwordInput.text.toString()

            val users = JsonUtils.loadUsers(this)
            val user = users.find { it.id == userId && it.passwd == password }
            
            // 로그인 성공 시에 Activity 1 실행
            if (user != null) {
                val intent = Intent(this, Activity1::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            } else { // 로그인 실패 시에 토스트 메세지 출력
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            finishAffinity()
        }
    }
}