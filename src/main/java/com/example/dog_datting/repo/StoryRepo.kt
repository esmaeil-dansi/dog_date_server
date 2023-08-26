package com.example.dog_datting.repo

import com.example.dog_datting.db.Story
import org.springframework.data.jpa.repository.JpaRepository

interface StoryRepo : JpaRepository<Story, String> {
}