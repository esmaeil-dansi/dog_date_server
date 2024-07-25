package com.example.dog_datting.controller

import com.example.dog_datting.db.AdminRequestType
import com.example.dog_datting.db.AdminRequests
import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Place
import com.example.dog_datting.dto.NewPlaceDto
import com.example.dog_datting.models.PlaceRes
import com.example.dog_datting.models.PlaceType
import com.example.dog_datting.repo.AdminRequestsRepo
import com.example.dog_datting.repo.LocationRepo
import com.example.dog_datting.repo.PlaceRepo
import com.example.dog_datting.repo.UserRepo
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
    private val adminRequestsRepo: AdminRequestsRepo,
    private val placeService: PlaceService,
    private val userRepo: UserRepo
) {
    val logger: Logger = LogManager.getLogger(PlaceController::class.java)

    @GetMapping(path = ["/fetchPlaces/{lastId}"])
    fun fetchPost(@PathVariable("lastId") lastId: Int): List<PlaceRes> {
        val places = placeRepo.findBySubmittedTrueAndIdGreaterThanOrderByIdDesc(lastId.toLong())
        val resList: MutableList<PlaceRes> = ArrayList()
        if (places != null) {
            for (place in places) {
                resList.add(placeService.convertPlaceToRes(place)!!)
            }
        }

        return resList
    }

    @GetMapping("/deletePlace/{id}")
    fun deletePlace(@PathVariable("id") id: Long): ResponseEntity<String> {
        try {
            placeRepo.deleteById(id)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().body(e.message)
        }
    }


    @PostMapping(path = ["/createNewPlace"])
    @ResponseBody
    fun createNewPlace(@RequestBody newPlaceDto: NewPlaceDto): ResponseEntity<Long?> {
        try {
            var owner = ""
            val user = userRepo.getUserByEmail(newPlaceDto.email)
            if (user != null) {
                owner = user.uuid;
            }
            var id: Long = 1
            val location = locationRepo.save(Location(lon = newPlaceDto.location.lon, lat = newPlaceDto.location.lat))
            val place = Place()
            place.description = newPlaceDto.description
            place.name = newPlaceDto.name
            place.owner = owner
            place.requester = newPlaceDto.requester
            place.location = location
            place.phone = newPlaceDto.phone
            place.palaceType = newPlaceDto.type
            place.fileUuid = newPlaceDto.fileUuid

            val usr = userRepo.getUserByUuid(newPlaceDto.requester);
            if (usr != null && usr.isAdmin) {
                place.submitted = true;
                id = (placeRepo.save(place)).id

            } else {
                val p = placeRepo.save(place)
                adminRequestsRepo.save(
                    AdminRequests(
                        time = System.currentTimeMillis(),
                        type = AdminRequestType.PLACE,
                        place = p,
                        requester = newPlaceDto.requester
                    )
                )
                id = p.id
            }


            return ResponseEntity.ok().body(id)
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }
}
