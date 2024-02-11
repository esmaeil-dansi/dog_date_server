package com.example.dog_datting.services

import com.example.dog_datting.db.Place
import com.example.dog_datting.models.PlaceRes
import com.example.dog_datting.repo.FileInfoRepo
import org.springframework.stereotype.Service

@Service
class PlaceService(private val fileInfoRepo: FileInfoRepo) {
    fun convertPlaceToRes(place: Place?): PlaceRes? {
        if (place != null) {
            val placeRes = PlaceRes()
            placeRes.description = place.description
            placeRes.id = place.id
            placeRes.name = place.name
            placeRes.owner = place.owner
            placeRes.type = place.palaceType
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
            return placeRes;
        }
        return null;

    }
}