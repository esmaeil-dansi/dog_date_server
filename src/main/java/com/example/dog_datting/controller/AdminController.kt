package com.example.dog_datting.controller

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.Settings
import com.example.dog_datting.db.User
import com.example.dog_datting.dto.SettingsDto
import com.example.dog_datting.models.AdminRequestsRes
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.FirebaseMessagingService
import com.example.dog_datting.services.PlaceService
import com.example.dog_datting.services.ShopService
import com.example.dog_datting.utils.JwtUtil
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
    private val placeService: PlaceService,
    private val settingRepo: SettingRepo,
    private val firebaseMessagingService: FirebaseMessagingService
) {
    val logger: Logger = LogManager.getLogger(MainController::class.java)

    private val jwtUtil: JwtUtil = JwtUtil()

    @PostMapping("/rejectRequest/{requestId}")
    fun rejectRequest(
        @PathVariable(value = "requestId") requestId: Long,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<String> {
        try {
            val requester = jwtUtil.getUid(authorizationHeader)
            val user: User? = userRepo.getUserByUuid(requester!!)
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

    @PostMapping(path = ["/updateSettings"])
    fun updateSetting(
        @RequestBody settingsDto: SettingsDto,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<String> {
        return try {
            val requester = jwtUtil.getUid(authorizationHeader)
            val user = userRepo.getUserByUuid(requester!!)
            if (user != null && user.isAdmin) {
                var record = settingRepo.findById(1)
                if (record.isPresent) {
                    settingRepo.save(
                        record.get().copy(
                            showAd = settingsDto.showAd,
                            openAppId = settingsDto.openAppId,
                            bannerId = settingsDto.bannerId,
                            adLoadingTimer = settingsDto.adLoadingTimer
                        )
                    )

                } else {
                    settingRepo.save(
                        Settings(
                            id = 1,
                            showAd = settingsDto.showAd,
                            openAppId = settingsDto.openAppId,
                            bannerId = settingsDto.bannerId,
                            adLoadingTimer = settingsDto.adLoadingTimer
                        )
                    )

                }
                ResponseEntity.ok().build()
            } else {
                ResponseEntity.badRequest().build()
            }


        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().body(e.message)
        }
    }

    @GetMapping("/fetchSettings")
    fun fetchSettings(): Settings? {
        return settingRepo.findById(1).get()
    }

    @PostMapping("/submitRequest/{requestId}")
    fun submitRequest(
        @PathVariable(value = "requestId") requestId: Long,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<String> {
        try {

            val user: User? = userRepo.getUserByUuid(jwtUtil.getUid(authorizationHeader)!!)
            if (user != null && user.isAdmin) {
                val request = adminRequestsRepo.findById(requestId)
                if (request.isPresent) {
                    adminRequestsRepo.delete(request.get())
                    return when (request.get().type) {
                        AdminRequestType.PLACE -> {
                            placeRepo.save(request.get().place!!.copy(submitted = true))
                            sendNotificationToPlaceOwner(request.get().place!!.owner)
                            ResponseEntity.ok().build()
                        }

                        AdminRequestType.SHOP -> {
                            shopRepo.save(request.get().shop!!.copy(submitted = true))
                            sendNotificationToShopOwner(request.get().shop!!.ownerId)
                            ResponseEntity.ok().build()
                        }

                        else -> {
                            doctorRepo.save(request.get().doctor!!.copy(submitted = true))
                            ResponseEntity.ok().build()
                        }
                    }
                }

            }
            return ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().build()
        }

    }

    private fun sendNotificationToPlaceOwner(userId: String) {
        try {
            val user = userRepo.getUserByUuid(userId)
            if (user != null) {
                firebaseMessagingService.sendNotification(
                    "New place",
                    "Your place registration request has been approved.",
                    user.firebaseToken
                )
            }


        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    private fun sendNotificationToShopOwner(userId: String) {
        try {
            val user = userRepo.getUserByUuid(userId)
            if (user != null) {
                firebaseMessagingService.sendNotification(
                    "New shop",
                    "Your shop registration request has been approved.",
                    user.firebaseToken
                )
            }


        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    @GetMapping("/fetchRequests/{requester}")
    fun fetchRequests(@RequestHeader("Authorization") authorizationHeader: String): List<AdminRequestsRes>? {
        try {
            val user: User? = userRepo.getUserByUuid(jwtUtil.getUid(authorizationHeader)!!)
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