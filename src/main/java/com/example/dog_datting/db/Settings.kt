package com.example.dog_datting.db

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Settings(
    @Id
    var id: Long = 0,
    var showAd: Boolean = true,
    var adLoadingTimer: Int = 10
)