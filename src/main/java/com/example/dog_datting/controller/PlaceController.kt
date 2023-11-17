package com.example.dog_datting.controller

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.AdminRequests
import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Place
import com.example.dog_datting.dto.NewPlaceDto
import com.example.dog_datting.dto.NotificationDto
import com.example.dog_datting.models.PlaceRes
import com.example.dog_datting.models.PlaceType
import com.example.dog_datting.repo.AdminRequestsRepo
import com.example.dog_datting.repo.FileInfoRepo
import com.example.dog_datting.repo.LocationRepo
import com.example.dog_datting.repo.PlaceRepo
import com.example.dog_datting.services.NotificationService
import com.example.dog_datting.services.PlaceService

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.ArrayList

@RestController
class PlaceController(
    private val placeRepo: PlaceRepo,
    private val locationRepo: LocationRepo,
    private val notificationService: NotificationService,
    private val adminRequestsRepo: AdminRequestsRepo,
    private val placeService: PlaceService
) {
    val logger: Logger = LogManager.getLogger(PlaceController::class.java)

    @GetMapping(path = ["/fetchPlaces/{lastId}"])
    fun fetchPost(@PathVariable("lastId") lastId: Int): List<PlaceRes> {
        val places = placeRepo.findBySubmittedTrueAndIdGreaterThanOrderByIdDesc(lastId.toLong())
        val postResList: MutableList<PlaceRes> = ArrayList()
        if (places != null) {
            for (place in places) {
                postResList.add(placeService.convertPlaceToRes(place))
            }
        }

        return postResList
    }

    private fun getType(key: String): PlaceType {
        when (key) {
            "ALL" -> return PlaceType.ALL
            "DOG" -> return PlaceType.DOG
            "CAT" -> return PlaceType.CAT
            "RABBIT" -> return PlaceType.RABBIT
            "HORSE" -> return PlaceType.HORSE

        }
        return PlaceType.ALL
    }


    @PostMapping(path = ["/createNewPlace"])
    @ResponseBody
    fun savePost(@RequestBody newPlaceDto: NewPlaceDto): ResponseEntity<Long?> {
        try {

            val location = locationRepo.save(Location(lon = newPlaceDto.location.lon, lat = newPlaceDto.location.lat))
            val place = Place()
            place.description = newPlaceDto.description
            place.name = newPlaceDto.name
            place.owner = newPlaceDto.owner
            place.location = location
            place.type = getType(newPlaceDto.type)
            place.fileUuid = newPlaceDto.fileUuid
            if (newPlaceDto.locationInfo != null) {
                val locationInfo =
                    locationRepo.save(Location(lon = newPlaceDto.locationInfo.lon, lat = newPlaceDto.locationInfo.lat))
                place.locationInfo = locationInfo

            }
            val p = placeRepo.save(place)
            notificationService.processNotification(
                NotificationDto(
                    location = newPlaceDto.location,
                    type = "NEWS",
                    fileInfo = "",
                    packetId = System.currentTimeMillis().toString(),
                    body = newPlaceDto.description
                ), sender = newPlaceDto.owner
            )
            adminRequestsRepo.save(
                AdminRequests(
                    time = System.currentTimeMillis(),
                    type = AdminRequestType.PLACE,
                    place = p,
                    requester = newPlaceDto.owner
                )
            )
            return ResponseEntity.ok().body(p.id)
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }
}