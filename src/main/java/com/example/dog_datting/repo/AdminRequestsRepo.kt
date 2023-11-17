package com.example.dog_datting.repo

import com.example.dog_datting.db.AdminRequests
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRequestsRepo : JpaRepository<AdminRequests, Long> {
}