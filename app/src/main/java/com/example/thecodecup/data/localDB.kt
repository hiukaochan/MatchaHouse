package com.example.thecodecup.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.*
import kotlinx.coroutines.flow.Flow

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

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique local ID

    val drinkName: String = "",
    val drinkImage: String = "",  // Store as URL or resource name/path
    val sweetness: String = "", // Example: "No sugar", "50%", "100%"
    val milkType: String = "",  // Example: "Whole milk", "Soy", "Almond"
    val iceLevel: String = "",  // Example: "No ice", "Less", "Normal"
    val quantity: Int,
    val totalAmount: Double // Total price for that item x quantity
)

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): kotlinx.coroutines.flow.Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT SUM(totalAmount) FROM cart_items")
    fun getCartTotal(): kotlinx.coroutines.flow.Flow<Double?>

    @Query("""
        SELECT * FROM cart_items 
        WHERE drinkName = :drinkName 
        AND drinkImage = :drinkImage 
        AND sweetness = :sweetness 
        AND milkType = :milkType 
        AND iceLevel = :iceLevel 
        LIMIT 1
    """)
    suspend fun findMatchingCartItem(
        drinkName: String,
        drinkImage: String,
        sweetness: String,
        milkType: String,
        iceLevel: String
    ): CartItem?

    @Update
    suspend fun updateCartItem(item: CartItem)
}


@Database(entities = [CartItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}