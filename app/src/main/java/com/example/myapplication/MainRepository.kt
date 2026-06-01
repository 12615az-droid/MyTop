package com.example.myapplication


import com.example.myapplication.database.BookDao

class MainRepository(
    private val mediaItemDao: BookDao
) {
    // Метод для сохранения в базу

}
