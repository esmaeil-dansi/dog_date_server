package com.example.dog_datting.controller

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.Advertising
import com.example.dog_datting.db.User
import com.example.dog_datting.dto.AdvertisingDto
import com.example.dog_datting.repo.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController(
    private val userRepo: UserRepo,
    private val adminRequestsRepo: AdminRequestsRepo,
    private val placeRepo: PlaceRepo,
    private val shopRepo: ShopRepo,
    private val advertisingRepo: AdvertisingRepo
) {
    val logger: Logger = LogManager.getLogger(MainController::class.java)

    @PostMapping("/rejectRequest/{requestId}/{requester}")
    fun rejectRequest(
        @PathVariable(value = "requester") requester: String,
        @PathVariable(value = "requestId") requestId: Long
    ): ResponseEntity<String> {
        try {
            val user: User? = userRepo.getUserByUuid(requester)
            if (user != null && user.isAdmin) {
                val request = adminRequestsRepo.findById(requestId)
                if (request.isPresent) {
                    return if (request.get().type == AdminRequestType.PLACE) {
                        adminRequestsRepo.delete(request.get())
                        placeRepo.delete(request.get().place!!)
                        ResponseEntity.ok().build()
                    } else {
                        adminRequestsRepo.delete(request.get())
                        shopRepo.delete(request.get().shop!!)
                        ResponseEntity.ok().build()
                    }
                }
            }
            return ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().build()
        }

    }

    @PostMapping("/submitRequest/{requestId}/{requester}")
    fun submitRequest(
        @PathVariable(value = "requester") requester: String,
        @PathVariable(value = "requestId") requestId: Long
    ): ResponseEntity<String> {
        try {
            val user: User? = userRepo.getUserByUuid(requester)
            if (user != null && user.isAdmin) {
                val request = adminRequestsRepo.findById(requestId)
                if (request.isPresent) {
                    adminRequestsRepo.delete(request.get())
                    return if (request.get().type == AdminRequestType.PLACE) {
                        placeRepo.save(request.get().place!!.copy(submitted = true))
                        ResponseEntity.ok().build()
                    } else {
                        shopRepo.save(request.get().shop!!.copy(submitted = true))
                        ResponseEntity.ok().build()
                    }


                }

            }
            return ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().build()
        }

    }

    @PostMapping("/createAdvertising/{requester}")
    fun createAdvertising(
        @RequestBody adv: AdvertisingDto,
        @PathVariable(value = "requester") requester: String,
    ): ResponseEntity<String>? {
        try {
            val user: User? = userRepo.getUserByUuid(requester)
            if (user != null && user.isAdmin) {
                advertisingRepo.save(
                    Advertising(
                        title = adv.title,
                        description = adv.description,
                        fileUuid = adv.fileUuid
                    )
                );
            }
            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }


}