package com.example.dog_datting.services

import com.example.dog_datting.db.Location
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class LocationService {
    fun checkLocation(userLocation: Location, location: Location): Boolean {
        return inNearRadius(
            location.lat,
            userLocation.lat,
            location.lon,
            userLocation.lon
        )
    }

    fun inNearRadius(
        lat1: Double, lat2: Double, lon1: Double,
        lon2: Double,
    ): Boolean {
        val r = 6371 // Radius of the earth
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = r * c * 1000 // convert to meters
        distance = distance.pow(2.0)
        return sqrt(distance) < 50000
    }
}