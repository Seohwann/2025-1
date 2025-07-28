package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class Activity3 : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val TAG = "Activity3"
    private var isRestaurantLocationSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        // 추가 기능 1: 구글 Map API활용
        // 구글 Map을 이용하기 위한 설정
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // 레스토랑 JSON 데이터 로드
        val restaurants = JsonUtils.loadRestaurants(this)
        val restaurantId = intent.getIntExtra("RESTAURANT_ID", 0)
        val restaurant = restaurants.find { it.id == restaurantId } ?: return

        // 유저 JSON 데이터 로드
        val users = JsonUtils.loadUsers(this)
        val userId = intent.getStringExtra("USER_ID") ?: return
        val user = users.find { it.id == userId } ?: return

        val thumbnail = findViewById<ImageView>(R.id.restaurantThumbnail)
        val nameText = findViewById<TextView>(R.id.restaurantName)
        val typeText = findViewById<TextView>(R.id.restaurantType)
        val locationText = findViewById<TextView>(R.id.restaurantLocation)
        val ratingText = findViewById<TextView>(R.id.restaurantRating)
        val descriptionText = findViewById<TextView>(R.id.restaurantDescription)
        val openingHoursText = findViewById<TextView>(R.id.restaurantOpeningHours)
        val menuList = findViewById<ListView>(R.id.menuList)
        val mapsLocation = findViewById<TextView>(R.id.mapsLocation)
        val restaurantWeather = findViewById<TextView>(R.id.restaurantWeather)
        val weatherImage = findViewById<ImageView>(R.id.weatherImage)
        val restaurantWeatherText = findViewById<TextView>(R.id.restaurantWeatherText)

        thumbnail.setImageResource(resources.getIdentifier(restaurant.image, "drawable", packageName))
        nameText.text = restaurant.restaurant
        typeText.text = restaurant.type
        locationText.text = restaurant.location
        ratingText.text = "Rating: ${restaurant.rating}"
        descriptionText.text = restaurant.description
        openingHoursText.text = "[${restaurant.openingHours.open}] ~ [${restaurant.openingHours.close}]"
        restaurantWeather.text = "Weather"
        mapsLocation.text = "Location"
        weatherImage.setImageResource(R.drawable.default_weather)

        //해당 레스토랑에 존재하는 메뉴를 어댑터로 구현하여 Listview로 표현
        val menuAdapter = MenuAdapter(this, restaurant.menu)
        menuList.adapter = menuAdapter

        // 추가 기능 2: Open Weather API활용
        // 해당 레스토랑의 위치에서의 날씨를 유저가 파악할 수 있도록 하여 날씨에 더 적합한 메뉴를 고를 수 있도록 했음
        // fetchWeather로 레스토랑의 위치에서의 값에 따른 날씨를 받아와서 restaurantWeatherText, weatherImage을 통해 나타냄
        fetchWeather(restaurant.location, restaurantWeatherText, weatherImage)
        
        // Reservation 버튼을 클릭하면 해당 레스토랑을 예약한다는 뜻이므로 다음 Activity로 intent를 통해서 데이터 전송
        val reservationButton = findViewById<Button>(R.id.reservationButton)
        reservationButton.setOnClickListener {
            val intent = Intent(this, Activity4::class.java)
            intent.putExtra("RESTAURANT_ID", restaurantId)
            intent.putExtra("USER_ID", user.id)
            startActivity(intent)
        }

        // back 버튼을 누르면 앱이 바로 전 Activity로 돌아가게 끔 설정
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    // 추가 기능 1: 구글 Map API활용
    // 구글 맵이 준비되면 호출되는 콜백 메서드 -> 기본 위치로 초기화 후 setRestaurantLocation를 통해서 레스토랑 위치를 가리켜
    // 사용자로 하여금 레스토랑의 대략적인 위치를 파악할 수 있도록 함
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        addMarker(0.0, 0.0, "initial address")
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 2f))
        setRestaurantLocation()
    }

    private fun setRestaurantLocation() {
        // intent로 넘겨받은 레스토랑의 위치를 읽어오기 위한 준비
        val restaurants = JsonUtils.loadRestaurants(this)
        val restaurantId = intent.getIntExtra("RESTAURANT_ID", 0)
        val restaurant = restaurants.find { it.id == restaurantId } ?: return
        thread {
            try {
                // geocoder를 이용해서 해당 위치의 주소를 경도와 위도로 변환
                val geocoder = Geocoder(this)
                val addresses = geocoder.getFromLocationName(restaurant.location, 1)
                if (!addresses.isNullOrEmpty()) { // 변환된 위치가 있다면
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    runOnUiThread {
                        // addMarker로 지도에 마커를 추가함
                        addMarker(latLng.latitude, latLng.longitude, restaurant.restaurant)
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                4f
                            )
                        ) // 해당 위치로 카메라를 움직임
                        isRestaurantLocationSet = true
                    }
                } else {
                    Log.w(TAG, "Geocoding failed for ${restaurant.location}")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Geocoding error: ${e.message}")
            }
        }
    }

    // marker를 통해서 Google map에서 해당 레스토랑의 이름을 확인할 수 있다.
    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        val markerOptions = MarkerOptions()
            .position(LatLng(latitude, longitude))
            .title(title)
        map.addMarker(markerOptions)
    }
    
    private fun fetchWeather(location: String, restaurantWeatherText: TextView, weatherImage: ImageView) {
        val client = OkHttpClient()
        // 해당 레스토랑의 위치를 인자로 받아서 해당 레스토랑의 위치에서의 날씨에 대한 정보를 Api에 요청함
        val url = "https://api.weatherapi.com/v1/current.json?key=MY_API_KEY&q=$location"

        thread {
            try {
                // Api요청을 보내고 요청에 대한 응답이 성공적으로 이루어졌다면 jsondata를 읽어와서
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val jsonData = response.body?.string() ?: "{}"
                        val jsonObject = JSONObject(jsonData)

                        val current = jsonObject.getJSONObject("current")
                        val temp = current.getDouble("temp_c")
                        val condition = current.getJSONObject("condition")
                        val description = condition.getString("text")

                        runOnUiThread {
                            // 날씨에 대한 설명과, 실제 온도, 온도에 따른 시각적 요소를 추가함을 통해 사용자에게 날씨에 맞는 음식을 먹을 수 있도록 도와주는 역할을 함
                            restaurantWeatherText.text =
                                "$description, ${String.format("%.1f", temp)}°C"
                            weatherImage.setImageResource(getWeatherImage(temp))
                        }
                    } else {
                        Log.e(TAG, "Weather API call failed for ${location}")
                    }
                }
            }
            catch (e: IOException) {
                Log.e(TAG, "Weather API call error: ${e.message}")
            }
        }
    }
    
    // 몇 도인지에 따라 그림으로 쉽게 파악하고 가시성을 향상시키기 위해 이미지와 온도를 연결해서 구현했음
    private fun getWeatherImage(temp: Double): Int {
        return when {
            temp >= 30 -> R.drawable.veryhot
            temp >= 20 -> R.drawable.hot
            temp >= 10 -> R.drawable.cool
            temp >= 0 -> R.drawable.chilly
            else -> R.drawable.cold
        }
    }

    private inner class MenuAdapter(
        private val context: Context,
        private val menuItems: List<MenuItem>
    ) : BaseAdapter() {
        override fun getCount(): Int = menuItems.size

        override fun getItem(position: Int): Any = menuItems[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.menu_list_item, parent, false)

            val menuItem = menuItems[position]
            val image = view.findViewById<ImageView>(R.id.menuImage)
            val nameText = view.findViewById<TextView>(R.id.menuNameText)
            val priceText = view.findViewById<TextView>(R.id.menuPriceText)

            image.setImageResource(resources.getIdentifier(menuItem.image, "drawable", packageName))
            nameText.text = menuItem.name
            priceText.text = "$ ${menuItem.price}"

            return view
        }
    }
}