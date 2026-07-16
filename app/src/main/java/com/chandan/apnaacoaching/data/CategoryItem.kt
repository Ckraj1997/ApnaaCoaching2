package com.chandan.apnaacoaching.data

data class CategoryItem(
    val group_id: String,
    val level_id: String,
    val category_id: String, // Note: the PHP script uses "category_id"
    val image_url: String,
    val title: String,
    val description: String
)
