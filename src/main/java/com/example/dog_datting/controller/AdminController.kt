package com.example.dog_datting.controller

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.Advertising
import com.example.dog_datting.db.User
import com.example.dog_datting.dto.AdvertisingDto
import com.example.dog_datting.models.AdminRequestsRes
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.PlaceService
import com.example.dog_datting.services.ShopService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AdminController(
    private val userRepo: UserRepo,
    private val adminRequestsRepo: AdminRequestsRepo,
    private val placeRepo: PlaceRepo,
    private val shopRepo: ShopRepo,
    private val doctorRepo: DoctorRepo,
    private val shopService: ShopService,
    private val placeService: PlaceService
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
                    } else if (request.get().type == AdminRequestType.SHOP) {
                        shopRepo.save(request.get().shop!!.copy(submitted = true))
                        ResponseEntity.ok().build()
                    } else {
                        doctorRepo.save(request.get().doctor!!.copy(submitted = true))
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

    @GetMapping("/fetchRequests/{requester}")
    fun fetchRequests(@PathVariable(value = "requester") requester: String): List<AdminRequestsRes>? {
        try {
            val user: User? = userRepo.getUserByUuid(requester)
            if (user != null && user.isAdmin) {
                val res = adminRequestsRepo.findAll()
                return res.map { e ->
                    AdminRequestsRes(
                        id = e.id,
                        type = e.type,
                        time = e.time,
                        doctor = e.doctor,
                        requester = e.requester,
                        place = placeService.convertPlaceToRes(place = e.place),
                        shop = shopService.shopMapper(e.shop),

                    )
                }

            }

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }


}