package com.example.thecodecup.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.*
import kotlinx.coroutines.flow.*
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

@Entity(tableName = "orders")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val drinkName: String,
    val drinkImage: String, // can be drawable name or URL
    val sweetness: String,
    val milkType: String,
    val iceLevel: String,
    val quantity: Int,
    val totalAmount: Double,
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var status: String = "ongoing" // or "completed"
)

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders WHERE status = 'ongoing' ORDER BY timestamp DESC")
    fun getOngoingOrders(): Flow<List<OrderItem>>

    @Query("SELECT * FROM orders WHERE status = 'completed' ORDER BY timestamp DESC")
    fun getCompletedOrders(): Flow<List<OrderItem>>

    @Insert
    suspend fun insertOrderItem(item: OrderItem)

    @Update
    suspend fun updateOrderItem(item: OrderItem)

    @Delete
    suspend fun deleteOrderItem(item: OrderItem)
}



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


@Database(entities = [CartItem::class, OrderItem::class, UserProfile::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userProfileDao(): UserProfileDao
    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffee_shop_db"
                ).fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).userProfileDao().upsertProfile(
                                    UserProfile(
                                        id = 1,
                                        fullName = "Your Name",
                                        phoneNumber = "+84000000000",
                                        email = "example@email.com",
                                        address = "Your Address"
                                    )
                                )
                            }
                        }
                    })
                    .build().also { instance = it }
            }
        }
    }

}

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 0,
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = ""
)

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfile)
}