package com.example.dog_datting.controller

import com.example.dog_datting.db.Animal
import com.example.dog_datting.db.Heath
import com.example.dog_datting.dto.HeathDto
import com.example.dog_datting.dto.NewAnimalDto
import com.example.dog_datting.models.Gender
import com.example.dog_datting.models.HeathRes
import com.example.dog_datting.repo.AnimalRepo
import com.example.dog_datting.repo.FileInfoRepo
import com.example.dog_datting.repo.HeathRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class AnimalController(
    private val animalRepo: AnimalRepo,
    private val heathRepo: HeathRepo,
    private val fileInfoRepo: FileInfoRepo
) {
    val logger: Logger = LogManager.getLogger(UserController::class.java)

    @PostMapping("/editAnimal")
    @ResponseBody
    fun editAnimal(@RequestBody animalDto: NewAnimalDto): ResponseEntity<String?> {
        try {
            animalRepo.save(
                Animal(
                    id = animalDto.id,
                    owner = animalDto.owner,
                    name = animalDto.name,
                    description = animalDto.description,
                    birthDay = animalDto.birthDay.toString(),
                    breed = animalDto.breed,
                    sex = Gender.valueOf(animalDto.sex),
                    passed = animalDto.passed,
                    death = animalDto.death.toString(),
                    neutered = animalDto.neutered,
                    lose = animalDto.lose,
                    avatarId = animalDto.avatarId,
                    uid = animalDto.uid
                )
            )
            return ResponseEntity.ok().build();
        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/addHeath/{animalUid}")
    @ResponseBody
    fun addHeath(
        @PathVariable(value = "animalUid") animalUid: String,
        @RequestBody heathDto: HeathDto
    ): ResponseEntity<String> {
        try {
            heathRepo.save(Heath(animalUid = animalUid, body = heathDto.body, fileUuid = heathDto.fileUuid))
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/getHeath/{animalUid}")
    @ResponseBody
    fun getHeath(@PathVariable(value = "animalUid") animalUid: String): List<HeathRes>? {
        var res: List<Heath>? = heathRepo.getByAnimalUid(animalUid)
        if (!res.isNullOrEmpty()) {
            var result: MutableList<HeathRes> = ArrayList()
            for (heath in res) {
                var h = HeathRes(id = heath.id, body = heath.body, animalUid = heath.animalUid)
                val info = fileInfoRepo.getByPacketId(heath.fileUuid)
                if (info != null) {
                    h.fileUuids = info.map { f -> f.uuid }
                }
                result.add(h)
            }
            return result
        }
        return null

    }


    @GetMapping("/getAnimals/{user}")
    @ResponseBody
    fun getAnimals(@PathVariable(value = "user") user: String): List<Animal>? {
        try {
            return animalRepo.getByOwner(user);

        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }


    @GetMapping("/deleteAnimal/{owner}/{id}")
    @ResponseBody
    fun deleteAnimal(
        @PathVariable(value = "owner") user: String,
        @PathVariable(value = "id") id: Long
    ): ResponseEntity<String?> {
        try {
            val a: Optional<Animal> = animalRepo.findById(id)
            if (a.isPresent && a.get().owner == user) {
                animalRepo.deleteById(id)
                return ResponseEntity.ok().build()
            }

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()
    }


    @PostMapping("/addNewAnimal")
    @ResponseBody
    fun addAnimal(@RequestBody animalDto: NewAnimalDto): ResponseEntity<Long?>? {
        try {
            var res = animalRepo.save(
                Animal(
                    owner = animalDto.owner,
                    name = animalDto.name,
                    description = animalDto.description,
                    birthDay = animalDto.birthDay.toString(),
                    breed = animalDto.breed,
                    sex = Gender.valueOf(animalDto.sex),
                    passed = animalDto.passed,
                    death = animalDto.death.toString(),
                    neutered = animalDto.neutered,
                    lose = animalDto.lose,
                    avatarId = animalDto.avatarId,
                    uid = animalDto.uid
                )
            )
            return ResponseEntity.ok().body(res.id)
        } catch (e: Exception) {
            logger.error(e)
        }
        return null

    }

}