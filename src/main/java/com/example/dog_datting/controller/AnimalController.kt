package com.example.dog_datting.controller

import com.example.dog_datting.db.Animal
import com.example.dog_datting.db.Heath
import com.example.dog_datting.db.Notifications
import com.example.dog_datting.dto.HeathDto
import com.example.dog_datting.dto.NewAnimalDto
import com.example.dog_datting.models.Gender
import com.example.dog_datting.models.HeathRes
import com.example.dog_datting.models.NotificationType
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.FirebaseMessagingService
import com.example.dog_datting.services.LocationService
import com.example.dog_datting.services.MessageService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.Executors


@RestController
class AnimalController(
    private val animalRepo: AnimalRepo,
    private val heathRepo: HeathRepo,
    private val fileInfoRepo: FileInfoRepo,
    private val notificationRepo: NotificationRepo,
    private val userRepo: UserRepo,
    private val messageService: MessageService,
    private val locationService: LocationService,
    private val firebaseMessagingService: FirebaseMessagingService

) {
    val logger: Logger = LogManager.getLogger(UserController::class.java)

    @PostMapping("/addNewAnimal")
    @ResponseBody
    fun addAnimal(@RequestBody animalDto: NewAnimalDto): ResponseEntity<Long?>? {
        try {
            val res = animalRepo.save(
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
            if (res.lose) {
                creteLostAnimalNotification(res)
            }

            return ResponseEntity.ok().body(res.id)
        } catch (e: Exception) {
            logger.error(e)
        }
        return null

    }


    @PostMapping("/editAnimal")
    @ResponseBody
    fun editAnimal(@RequestBody animalDto: NewAnimalDto): ResponseEntity<String?> {
        try {
            val animal = animalRepo.findById(animalDto.id);
            if (animal.isPresent) {
                val a = animalRepo.save(
                    animal.get().copy(
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
                if (animalDto.lose) {
                    creteLostAnimalNotification(a)
                } else {
                    deleteNotificationOnAnimal(a)
                }
            }


            return ResponseEntity.ok().build();
        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.internalServerError().build();
    }

    private fun creteLostAnimalNotification(animal: Animal) {
        try {
            val executor = Executors.newFixedThreadPool(2)
            executor.execute {
                val user = userRepo.getUserByUuid(animal.owner);
                if (user?.location != null) {
                    userRepo.findByLocationIsNotNull()?.forEach { u ->
                        if (locationService.checkLocation(u.location!!, user.location!!)) {
                            val notifications = Notifications(
                                animalId = animal.id,
                                receiver = u.uuid,
                                type = NotificationType.LOST_ANIMAL,
                                time = System.currentTimeMillis()
                            )
                            logger.info("send & save  notification\n")
                            messageService.sendNotification(
                                notifications = notificationRepo.save(notifications),
                                to = u.uuid
                            )
                            if (u.firebaseToken.isNotEmpty()) {
                                firebaseMessagingService.sendNotification(
                                    "Lost",
                                    "A animal lost !",
                                    u.firebaseToken
                                )
                            }

                        }
                    }
                }
            }


        } catch (e: Exception) {
            logger.error(e)
        }

    }

    private fun deleteNotificationOnAnimal(animal: Animal) {
        try {
            notificationRepo.getByAnimalId(animalId = animal.id)?.forEach { s ->
                run {
                    notificationRepo.deleteById(s.id)
                }
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    @GetMapping("/getAnimal/{id}")
    @ResponseBody
    fun getAnimal(@PathVariable(value = "id") id: Long): Optional<Animal> {
        return animalRepo.findById(id)
    }

    @PostMapping("/addHeath/{animalUid}")
    @ResponseBody
    fun addHeath(
        @PathVariable(value = "animalUid") animalUid: String,
        @RequestBody heathDto: HeathDto
    ): ResponseEntity<Long> {
        try {
           var res =  heathRepo.save(Heath(animalUid = animalUid, body = heathDto.body, fileUuid = heathDto.fileUuid))
            return ResponseEntity.ok().body(res.id)
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

    @GetMapping("/deleteHeath/{id}")
    fun deleteHeath(@PathVariable(value = "id") id: Long): ResponseEntity<String> {
        try {
            heathRepo.deleteById(id)
            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e.message)
            return ResponseEntity.internalServerError().body(e.message)
        }
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


}
