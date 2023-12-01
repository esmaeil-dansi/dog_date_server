package com.example.dog_datting.repo

import com.example.dog_datting.db.Settings
import org.springframework.data.jpa.repository.JpaRepository

interface SettingRepo : JpaRepository<Settings, Long> {
}