# MatchaHouse ☕📱

**MatchaHouse** is a multi-screen Android application developed in **Kotlin**. It simulates the digital operations of a modern matcha beverage shop, offering a complete customer experience from browsing drinks to redeeming loyalty rewards. The app uses **Room** for local data persistence and follows modular design practices with reactive programming via **Kotlin Flows**.

---

## 🧩 Features

### 📱 UI Screens
- **Home**: Personalized greeting, loyalty stamp display, and a grid of available matcha drinks.
- **Details**: Customize drink options (sweetness, milk type, ice level, quantity) and add to cart or favorites.
- **Cart**: View cart contents, adjust quantities, swipe to remove, and checkout.
- **Order Success**: Confirmation screen with navigation to order tracking.
- **My Orders**: View ongoing and completed orders, with swipe-to-complete interaction.
- **Rewards**: Track loyalty stamps and redeem points for free drinks.
- **Redeem Rewards**: Exchange points for items with automatic deduction.
- **Profile**: Edit user profile data while preserving points and drink count.
- **Favorites**: Save and manage customized drink preferences with swipe-to-remove.

---

## 💾 Room Database Schema

The app uses a local Room database with the following entities:

- `Drink`: Predefined static list of matcha drinks (not stored in DB).
- `CartItem`: Stores drinks added to the cart with customization details.
- `OrderItem`: Tracks both ongoing and completed orders.
- `PointsHistory`: Logs reward points earned from purchases.
- `FavoriteDrink`: Saves unique drink configurations.
- `UserProfile`: Stores user information, loyalty progress, and preferences.

Each screen or activity interacts with the database through its corresponding **DAO (Data Access Object)**, ensuring clean and testable data operations.

---

## ✨ Additional Features

- First-time users are redirected to the **Profile** screen to update shipping info.
- **Favorites** Screen for saving favourite drink selection with **dynamic favorite icon** updates based on current selection state.
- Identical cart items are merged by incrementing quantity instead of duplication.
- Lightweight log messages are used to notify user actions (e.g., added to cart, redeemed).

---

## 🚀 Technologies Used

- **Kotlin**
- **Room (Jetpack)**
- **Kotlin Coroutines & Flow**
- **Android Studio**

---

## 📷 Demo video
https://youtube.com/shorts/mfxMIhKMuHw

