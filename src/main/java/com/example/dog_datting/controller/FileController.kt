package com.example.dog_datting.controller

import com.example.dog_datting.db.FileInfo
import com.example.dog_datting.repo.FileInfoRepo
import com.example.dog_datting.services.MinioService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream
import java.util.*

@RestController
class FileController {
    val logger: Logger = LogManager.getLogger(FileController::class.java)

    val AVATAR: String = "avatar"
    val MESSAGE: String = "message";

    @Autowired
    private lateinit var fileInfoRepo: FileInfoRepo

    @Autowired
    private lateinit var minioService: MinioService

    @GetMapping("download/{uuid}")
    @ResponseBody
    fun downloadFile(@PathVariable(value = "uuid") uuid: String): ResponseEntity<Resource>? {
        try {
            val file = minioService.getFile(uuid)
            if (file != null) {
                return ResponseEntity.ok().contentLength(file.length())
                    .body(InputStreamResource(FileInputStream(file)))
            }

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }


    @GetMapping("getFile/{uuid}")
    @ResponseBody
    fun getFile(@PathVariable(value = "uuid") uuid: String): ResponseEntity<Resource>? {
        try {
            val file = minioService.getFile(uuid, bucket = MESSAGE)
            if (file != null) {
                return ResponseEntity.ok().contentLength(file.length())
                    .body(InputStreamResource(FileInputStream(file)))
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }

    @GetMapping("downloadAvatar/{uuid}")
    @ResponseBody
    fun downloadAvatar(@PathVariable(value = "uuid") uuid: String): ResponseEntity<Resource>? {
        try {
            val file = minioService.getFile(uuid, bucket = AVATAR)
            if (file != null) {
                return ResponseEntity.ok().contentLength(file.length())
                    .body(InputStreamResource(FileInputStream(file)))
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }

    @PostMapping(value = ["/upload/{fileUuid}"], consumes = ["multipart/form-data"])
    @ResponseBody
    fun uploadFiles(
        @RequestPart("file") file: MultipartFile,
        @PathVariable(value = "fileUuid") fileUuid: String
    ): ResponseEntity<String>? {
        return try {
            val uuid = UUID.randomUUID().toString() + "_" + file.originalFilename
            minioService.saveFile(file.bytes, uuid)
            fileInfoRepo.save(FileInfo(uuid = uuid, packetId = fileUuid, name = file.originalFilename!!))
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message);
            ResponseEntity.internalServerError().body(e.message)
        }
    }

    @PostMapping(value = ["/uploadMessageFile"], consumes = ["multipart/form-data"])
    @ResponseBody
    fun uploadFileMessage(
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<String>? {
        return try {
            val uuid = UUID.randomUUID().toString() + "_" + file.originalFilename
            minioService.saveFile(file.bytes, uuid, bucket = MESSAGE)
            ResponseEntity.ok().body(uuid)
        } catch (e: Exception) {
            logger.error(e.message);
            ResponseEntity.internalServerError().body(e.message)
        }
    }


    @PostMapping(value = ["/saveAvatar/{uuid}"], consumes = ["multipart/form-data"])
    @ResponseBody
    fun saveAvatar(
        @RequestPart("file") file: MultipartFile,
        @PathVariable(value = "uuid") uuid: String
    ): ResponseEntity<String>? {
        return try {
            minioService.saveFile(file.bytes, uuid, bucket = AVATAR)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message);
            ResponseEntity.internalServerError().body(e.message)
        }
    }

}