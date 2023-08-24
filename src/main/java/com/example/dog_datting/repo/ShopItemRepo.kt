package com.example.dog_datting.repo

import com.example.dog_datting.db.ShopItems
import org.springframework.data.jpa.repository.JpaRepository

interface ShopItemRepo : JpaRepository<ShopItems, Long> {
}