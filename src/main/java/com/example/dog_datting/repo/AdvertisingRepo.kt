package com.example.dog_datting.repo

import com.example.dog_datting.db.Advertising
import org.springframework.data.jpa.repository.JpaRepository

interface AdvertisingRepo:JpaRepository<Advertising,Long> {
}