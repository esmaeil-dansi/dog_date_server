package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.dto.DoctorDto
import com.example.dog_datting.dto.RateDoctorDto
import com.example.dog_datting.repo.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DoctorController(
    private val locationRepo: LocationRepo,
    private val doctorRepo: DoctorRepo,
    private val doctorLikeRepo: DoctorLikeRepo,
    private val adminRequestsRepo: AdminRequestsRepo,
    private val userRepo: UserRepo
) {
    val logger: Logger = LogManager.getLogger(MainController::class.java)


    @PostMapping(path = ["/saveDoctor"])
    @ResponseBody
    fun saveDoctor(@RequestBody doctorDto: DoctorDto): ResponseEntity<Long> {
        try {

            val doctor = doctorRepo.save(
                Doctor(
                    ownerId = doctorDto.ownerId,
                    name = doctorDto.name,
                    description = doctorDto.description,
                    avatarInfo = doctorDto.avatarInfo,
                    location = locationRepo.save(Location(lat = doctorDto.location.lat, lon = doctorDto.location.lon)),
                    locationDetails = doctorDto.locationDetails
                )
            )
            val user = userRepo.getUserByUuid(doctorDto.ownerId)
            if (user != null && user.isAdmin) {
                doctor.submitted = true
                doctorRepo.save(doctor)
                return ResponseEntity.ok().body(doctor.id)
            } else {
                adminRequestsRepo.save(
                    AdminRequests(
                        time = System.currentTimeMillis(),
                        type = AdminRequestType.DOCTOR,
                        doctor = doctor,
                        requester = doctorDto.ownerId
                    )
                );
                return ResponseEntity.ok().body(doctor.id)
            }


        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("fetchDoctors/{lastId}")
    @ResponseBody
    fun fetchDoctors(@PathVariable(value = "lastId") lastId: String): List<Doctor>? {
        try {
            return doctorRepo.findBySubmittedTrue()
        } catch (e: Exception) {
            print(e.message)
        }
        return null
    }


    @PostMapping(path = ["/rateDoctor"])
    @ResponseBody
    fun rateDoctor(@RequestBody rateDoctorDto: RateDoctorDto): ResponseEntity<Int?> {
        return try {

            val doctor = doctorRepo.findById(rateDoctorDto.id)
            if (doctor.isPresent) {
                val d: Doctor = doctor.get()

                val dr: DoctorLikes? =
                    doctorLikeRepo.getByUserIdAndDoctorId(doctorId = d.id, userId = rateDoctorDto.requester)
                if (dr != null) {
                    doctorLikeRepo.delete(dr)
                }
                val counts: Int = doctorLikeRepo.countGetByDoctorId(d.id)
                if (counts > 0) {
                    d.rate = ((rateDoctorDto.rate + ((d.rate) * counts))) / (counts + 1)
                } else {
                    d.rate = rateDoctorDto.rate
                }
                doctorLikeRepo.save(DoctorLikes(doctorId = d.id, userId = rateDoctorDto.requester))
                doctorRepo.save(d)
                return ResponseEntity.ok().body(d.rate)
            }
            ResponseEntity.internalServerError().build()

        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build()

        }
    }
}