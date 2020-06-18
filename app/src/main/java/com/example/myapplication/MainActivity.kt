package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.dto.CurrencyResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var byn: EditText
    private lateinit var eur: EditText
    private lateinit var usd: EditText
    private lateinit var rub: EditText
    private lateinit var pln: EditText
    private lateinit var currencies: List<CurrencyResponseDTO>
    private val currenciesTextFields: HashMap<CurrencyResponseDTO, EditText> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        byn = findViewById(R.id.BYN)
        eur = findViewById(R.id.EUR)
        usd = findViewById(R.id.USD)
        rub = findViewById(R.id.RUB)
        pln = findViewById(R.id.PLN)
        val httpClient = OkHttpClient()
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
                    currencies.forEach {
                        when(it.Cur_Abbreviation) {
                            "EUR" -> currenciesTextFields[it] = eur
                            "USD" -> currenciesTextFields[it] = usd
                            "RUB" -> currenciesTextFields[it] = rub
                            "PLN" -> currenciesTextFields[it] = pln
                        }
                    }

                    currenciesTextFields.put(
                        CurrencyResponseDTO(1, Date().toString(),
                        "BYN", 1.0, "Belorussian lives",
                            1.0),
                        byn)



                    currenciesTextFields.keys.stream()
                        .forEach{ inputKey ->

                            currenciesTextFields[inputKey]?.setImeActionLabel("keyUp", KeyEvent.ACTION_UP)

                            currenciesTextFields[inputKey]?.setOnKeyListener { view, actionId, keyEvent ->
                                when (keyEvent.action) {
                                    KeyEvent.ACTION_UP -> {

                                        currenciesTextFields.keys.stream()
                                            .filter{inputKey.Cur_Abbreviation != it.Cur_Abbreviation}
                                            .forEach{keyToChange ->

                                                val inputText = currenciesTextFields[inputKey]?.text

                                                if (inputText.isNullOrBlank()) {
                                                    return@forEach
                                                }

                                                val inputRate: Double = inputKey.Cur_OfficialRate
                                                val inputScale: Double = inputKey.Cur_Scale
                                                val toChangeRate: Double = keyToChange.Cur_OfficialRate
                                                val toChangeScale: Double = keyToChange.Cur_Scale
                                                val inputValue: Double = currenciesTextFields[inputKey]?.text.toString().toDouble()

                                                val valueToSet: String =
                                                    ((inputValue * (inputRate / inputScale)) / (toChangeRate / toChangeScale))
                                                        .toBigDecimal()
                                                        .setScale(3, 3)
                                                        .toString()

                                                currenciesTextFields[keyToChange]?.setText(valueToSet)
                                            }

                                        true
                                    }
                                    else -> false
                                }
                            }
                        }

                }
            }
        }
        httpClient.newCall(request).enqueue(implCallback)
    }
}
