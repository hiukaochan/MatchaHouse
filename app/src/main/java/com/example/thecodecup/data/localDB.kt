package com.example.thecodecup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Drink(
    @PrimaryKey val id: Int = 0,
    val name: String = "",
    val price: Int = 0, // in VND
    val drawableName: String = ""
)

val drinkList = listOf(
    Drink(1, "Matcha Latte", 65000, "matcha_latte"),
    Drink(2, "Matcha Strawberry", 70000, "matcha_straw"),
    Drink(3, "Matcha Gelato", 75000, "matcha_gelato"),
    Drink(4, "Usucha", 89000, "usucha")
)