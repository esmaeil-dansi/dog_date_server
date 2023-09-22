package com.example.dog_datting.controller

import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Place
import com.example.dog_datting.dto.NewPlaceDto
import com.example.dog_datting.dto.NotificationDto
import com.example.dog_datting.models.PlaceRes
import com.example.dog_datting.repo.FileInfoRepo
import com.example.dog_datting.repo.LocationRepo
import com.example.dog_datting.repo.PlaceRepo
import com.example.dog_datting.services.NotificationService

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.ArrayList

@RestController
class PlaceController(
    private val placeRepo: PlaceRepo,
    private val fileInfoRepo: FileInfoRepo,
    private val locationRepo: LocationRepo,
    private val notificationService: NotificationService
) {
    val logger: Logger = LogManager.getLogger(PlaceController::class.java)

    @GetMapping(path = ["/fetchPlaces/{lastId}"])
    fun fetchPost(@PathVariable("lastId") lastId: Int): List<PlaceRes> {
        val places = placeRepo.findByIdGreaterThanOrderByIdDesc(lastId.toLong())
        val postResList: MutableList<PlaceRes> = ArrayList()
        if (places != null) {
            for (place in places) {
                val placeRes = PlaceRes()
                placeRes.description = place.description
                placeRes.id = place.id
                placeRes.name = place.name
                placeRes.owner = place.owner
                placeRes.location =
                    com.example.dog_datting.models.Location(lat = place.location.lat, lon = place.location.lon)
                val info = fileInfoRepo.getByPacketId(place.fileUuid)
                if (info != null) {
                    placeRes.fileUuids = info.map { f -> f.uuid }
                }
                if (place.locationInfo != null) {
                    placeRes.locationInfo = com.example.dog_datting.models.Location(
                        lat = place.locationInfo!!.lat,
                        lon = place.locationInfo!!.lon
                    )
                }
                postResList.add(placeRes)
            }
        }

        return postResList
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
            );
            return ResponseEntity.ok().body(p.id)
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }
}