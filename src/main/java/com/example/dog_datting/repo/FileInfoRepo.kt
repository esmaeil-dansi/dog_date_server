package com.example.dog_datting.repo

import com.example.dog_datting.db.FileInfo
import org.springframework.data.jpa.repository.JpaRepository

interface FileInfoRepo : JpaRepository<FileInfo, String> {

    fun getByUuid(uuid: String): FileInfo?
    fun getByPacketId(packetId: String): List<FileInfo>?

}