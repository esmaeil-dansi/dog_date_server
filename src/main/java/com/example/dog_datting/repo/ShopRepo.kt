package com.example.dog_datting.repo

import com.example.dog_datting.db.Shop
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRepo : JpaRepository<Shop, Long> {
    fun findBySubmittedTrueAndIdGreaterThanOrderByIdDesc(id: Long): List<Shop>?
}