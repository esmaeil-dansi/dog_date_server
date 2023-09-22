package com.example.dog_datting.repo

import com.example.dog_datting.db.Friends
import com.example.dog_datting.db.User

import org.springframework.data.jpa.repository.JpaRepository

interface FriendRepo : JpaRepository<Friends, Long> {

    fun findByOwner(owner: User): List<Friends>?

    fun findByUser(user: User): List<Friends>?
}