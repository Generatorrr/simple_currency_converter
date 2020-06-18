package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.dto.CurrencyResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var byn: EditText
    private lateinit var eur: EditText
    private lateinit var usd: EditText
    private lateinit var rub: EditText
    private lateinit var calculateButton: Button
    private lateinit var textResponse: String
    private lateinit var currencies: List<CurrencyResponseDTO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        byn = findViewById(R.id.BYN)
        eur = findViewById(R.id.EUR)
        usd = findViewById(R.id.USD)
        rub = findViewById(R.id.RUB)
        calculateButton = findViewById(R.id.button)
        val httpClient: OkHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url("https://www.nbrb.by/api/exrates/rates?periodicity=0")
            .build()

        val implCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseString = response.body()?.string()
                    val gson = Gson()

                    val myType = object : TypeToken<List<CurrencyResponseDTO>>() {}.type

                    currencies = gson.fromJson(responseString, myType)
                    println(currencies)

                    val sosat: CurrencyResponseDTO =
                        currencies.stream()
                            .filter { item -> item.Cur_Abbreviation == "EUR" }
                            .findFirst()
                            .orElseGet(null)
                    textResponse = sosat.Cur_Abbreviation

                    this@MainActivity.runOnUiThread {
                        textView.setText(textResponse)
                    }
                }
            }
        }
        httpClient.newCall(request).enqueue(implCallback)
    }
}
