package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.db.Location
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.AdminRequestsRes
import com.example.dog_datting.models.ShopRes
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.PlaceService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class MainController(
    private val doctorLikeRepo: DoctorLikeRepo,
    private val adminRequestsRepo: AdminRequestsRepo,
    private val userRepo: UserRepo,
    private val advertisingRepo: AdvertisingRepo,
    private val placeService: PlaceService,
    private val settingRepo: SettingRepo
) {

    val logger: Logger = LogManager.getLogger(MainController::class.java)


    @Autowired
    private lateinit var commentRepo: CommentRepo

    @Autowired
    private lateinit var locationRepo: LocationRepo


    @Autowired
    private lateinit var doctorRepo: DoctorRepo


    @Autowired
    private lateinit var storyRepo: StoryRepo

    @GetMapping("fetchDoctors/{lastId}")
    @ResponseBody
    fun fetchDoctors(@PathVariable(value = "lastId") lastId: String): List<Doctor>? {
        try {
            return doctorRepo.findAll()
        } catch (e: Exception) {
            print(e.message)
        }
        return null
    }


    @GetMapping(path = ["/fetchComments/{postId}"])
    fun fetchComments(@PathVariable("postId") postId: Int): List<Comment>? {
        try {
            return commentRepo.getCommentByPostIdOrderByTimeDesc(postId = postId.toString())
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null

    }

    @PostMapping(path = ["/updateSettings"])
    fun updateSetting(@RequestBody settingsDto: SettingsDto): ResponseEntity<String> {
        return try {
            settingRepo.save(Settings(id = 0, showAd = settingsDto.showAd, adLoadingTimer = settingsDto.adLoadingTimer))
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().body(e.message);
        }
    }

    @GetMapping("/fetchSettings")
    fun fetchSettings(): Settings? {
        return settingRepo.findById(0).get()
    }

    @PostMapping(path = ["/saveDoctor"])
    @ResponseBody
    fun saveDoctor(@RequestBody doctorDto: DoctorDto): ResponseEntity<String?> {
        try {
            val location = locationRepo.save(Location(lat = doctorDto.location.lat, lon = doctorDto.location.lat))
            var locationInfo: Location? = null;
            if (doctorDto.locationInfo != null) {
                val l =
                    locationRepo.save(Location(lon = doctorDto.locationInfo.lon, lat = doctorDto.locationInfo.lat))
                locationInfo = l

            }
            doctorRepo.save(
                Doctor(
                    ownerId = doctorDto.ownerId,
                    name = doctorDto.name,
                    locationInfo = locationInfo,
                    description = doctorDto.description,
                    avatarInfo = doctorDto.avatarInfo,
                    location = location,
                    locationDetails = doctorDto.locationDetails
                )
            )
            return ResponseEntity.ok().build();

        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping(path = ["/rateDoctor"])
    @ResponseBody
    fun rateDoctor(@RequestBody rateDoctorDto: RateDoctorDto): ResponseEntity<Int?> {
        return try {

            val doctor = doctorRepo.findById(rateDoctorDto.ownerId)
            if (doctor.isPresent) {
                val d: Doctor = doctor.get();

                val dr: DoctorLikes? =
                    doctorLikeRepo.getByUserIdAndDoctorId(doctorId = d.ownerId, userId = rateDoctorDto.requester)
                if (dr != null) {
                    doctorLikeRepo.delete(dr)
                }
                val counts: Int = doctorLikeRepo.countGetByDoctorId(d.ownerId)
                if (counts > 0) {
                    d.rate = ((rateDoctorDto.rate + ((d.rate) * counts))) / (counts + 1)
                } else {
                    d.rate = rateDoctorDto.rate
                }
                doctorLikeRepo.save(DoctorLikes(doctorId = d.ownerId, userId = rateDoctorDto.requester))
                doctorRepo.save(d)
                return ResponseEntity.ok().body(d.rate)
            }
            ResponseEntity.internalServerError().build()

        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build();

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
                        requester = e.requester,
                        place = placeService.convertPlaceToRes(place = e.place),
                        shop = e.shop
                    )
                }

            }

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }

    @GetMapping("/fetchAdvertising")
    fun fetchAdvertising(): List<Advertising>? {
        try {
            return advertisingRepo.findAll();
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }


    @PostMapping(path = ["/saveStory"])
    @ResponseBody
    fun saveStory(@RequestBody storyDto: StoryDto): ResponseEntity<String>? {
        return try {
            storyRepo.save(
                Story(
                    userId = storyDto.userId,
                    fileInfo = storyDto.fileInfo,
                    description = storyDto.description,
                    time = storyDto.time
                )
            )
            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping(path = ["/fetchStory/{userId}"])
    @ResponseBody
    fun fetchStory(@PathVariable(value = "userId") userId: String): Story? {
        try {
            return storyRepo.findById(userId).get()
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }
}