package com.example.myapplication.dto

data class CurrencyResponseDTO(
    val Cur_ID: Int,
    val Date: String,
    val Cur_Abbreviation: String,
    val Cur_Scale: Double,
    val Cur_Name: String,
    val Cur_OfficialRate: Double
)