package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.Gender
import com.example.dog_datting.models.NotificationType
import com.example.dog_datting.models.UserId
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.EmailService
import com.example.dog_datting.services.MessageService
import com.example.dog_datting.services.UserService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class UserController(
    private val userRepo: UserRepo,
    private val userService: UserService,
    private val galleryRepo: GalleryRepo,
    private val notificationRepo: NotificationRepo,
    private val friendRepo: FriendRepo,
    private val messageService: MessageService,
    private val emailService: EmailService,
    private val locationRepo: LocationRepo,
    private val animalRepo: AnimalRepo,
    private val topicRepo: TopicRepo
) {

    val logger: Logger = LogManager.getLogger(UserController::class.java)

    @PostMapping(path = ["/login"])
    @ResponseBody
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<UserId?> {
        try {
            var user: User? = null
            if (loginDto.email.isNotEmpty()) {
                user = userRepo.getUserByEmail(loginDto.email)
            }
            return if (user != null) {
                if (user.password == loginDto.password) {
                    ResponseEntity.ok(UserId(id = user.uuid, username = user.username, name = user.firstname))
                } else {
                    ResponseEntity.badRequest().build()
                }
            } else {
                ResponseEntity.notFound().build()
            }

        } catch (ignored: Exception) {
        }
        return ResponseEntity.internalServerError().build()

    }

    @PostMapping(path = ["/createTopic/{topic}"])
    @ResponseBody
    fun createTopic(@PathVariable(value = "topic") topic: String): ResponseEntity<String> {
        try {
            val topics: Topics? = topicRepo.getByName(topic)
            if (topics == null) {
                topicRepo.save(Topics(name = topic))
            }
            return ResponseEntity.ok().build()
        } catch (ignored: Exception) {
        }
        return ResponseEntity.badRequest().build()
    }

    @PostMapping(path = ["/singingByPhoneNumber"])
    @ResponseBody
    fun singing(@RequestBody loginByPhoneNumberDto: PhoneNumberDto): ResponseEntity<String> {
        try {
            val user: User? = userRepo.getUserByPhoneNumber(loginByPhoneNumberDto.phoneNumber)
            return if (user != null) {
                ResponseEntity.ok().body("INSERT_PASSWORD")
            } else {
                val rand = Random()
                val code = rand.nextInt(5)
                userService.sendVerificationCodeToPhone(loginByPhoneNumberDto.phoneNumber, code.toString())
                val user = User()
                user.phoneNumber = loginByPhoneNumberDto.phoneNumber
                user.verificationCode = code
                userRepo.save(user)
                ResponseEntity.ok().body("REGISTER")
            }
        } catch (ignored: Exception) {
        }
        return ResponseEntity.badRequest().build()
    }

    @PostMapping(path = ["/singingByEmail"])
    @ResponseBody
    fun singingByEmail(@RequestBody loginByEmailDto: EmailDto): ResponseEntity<String> {
        try {
            val u: User? = userRepo.getUserByEmail(loginByEmailDto.email)

            return if (u != null && u.uuid.isNotEmpty()) {
                ResponseEntity.badRequest().build()
            } else {
                val code = kotlin.random.Random.nextInt(10000, 99999)
                if (u == null) {
                    val user = User()
                    logger.info("verification cede\t" + code)
                    user.email = loginByEmailDto.email
                    user.username = loginByEmailDto.username
                    user.firstname = loginByEmailDto.name
                    user.password = loginByEmailDto.password
                    user.verificationCode = code
                    emailService.sendLoginCodeEmail(email = loginByEmailDto.email, code = code.toString())
                    userRepo.save(user)
                } else {
                    u.verificationCode = code
                    u.password = loginByEmailDto.password
                    emailService.sendLoginCodeEmail(email = loginByEmailDto.email, code = code.toString())
                    userRepo.save(u)
                }

                ResponseEntity.ok().build()
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()
    }


    @GetMapping("/getAllTopics")
    @ResponseBody
    fun getAllTopics(

    ): List<Topics>? {
        try {
            return topicRepo.findAll();

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }

    @GetMapping("/checkUsername/{user}/{username}")
    @ResponseBody
    fun checkUsernameIsExit(
        @PathVariable(value = "username") username: String,
        @PathVariable(value = "user") uid: String
    ): ResponseEntity<String?> {
        try {
            val user: User? = userRepo.getUserByUsername(username = username)
            if (user == null || user.uuid == uid) {
                return ResponseEntity.ok().build()
            }
            return ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/getUsername/{uuid}")
    @ResponseBody
    fun getUsername(@PathVariable(value = "uuid") uuid: String): ResponseEntity<String?> {
        try {
            val user: User? = userRepo.getUserByUuid(uuid)
            if (user != null) {
                return ResponseEntity.ok().body(user.username)
            }
            return ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/recoveryEmail/{email}")
    @ResponseBody
    fun sendRecoveryEmail(@PathVariable(value = "email") email: String): ResponseEntity<String> {
        var user: User? = userRepo.getUserByEmail(email = email)
        if (user != null) {
            val recoverCode = kotlin.random.Random.nextInt(10000, 99999)
            user.recoveryCode = recoverCode
            userRepo.save(user)
            emailService.sendVerifyCodeEmail(email = email, code = recoverCode.toString())
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.badRequest().build()
    }

    @PostMapping(path = ["/sendVerificationCode"])
    @ResponseBody
    fun sendVerificationCode(@RequestBody verificationDto: VerificationDto): ResponseEntity<String> {
        try {
            val u: User? = userRepo.getUserByEmail(verificationDto.user)
            logger.info("sendVerificationCode")

            return if (u != null && u.verificationCode == verificationDto.code) {
                val uuid = UUID.randomUUID().toString();
                u.uuid = uuid
                userRepo.save(u)
                ResponseEntity.ok().body(u.uuid)
            } else {
                ResponseEntity.badRequest().build()
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()
    }


    @PostMapping(path = ["/recovery"])
    @ResponseBody
    fun recovery(@RequestBody recoveryDto: RecoveryDto): ResponseEntity<String> {
        try {
            val u: User? = userRepo.getUserByEmail(recoveryDto.email)

            return if (u != null && u.recoveryCode == recoveryDto.code) {
                u.password = recoveryDto.password
                userRepo.save(u)
                ResponseEntity.ok().body(u.uuid)
            } else {
                ResponseEntity.badRequest().build()
            }
        } catch (ignored: Exception) {
        }
        return ResponseEntity.internalServerError().build()
    }


    @GetMapping("/getAllUser")
    @ResponseBody
    fun getAllUser(): List<User>? {
        try {
            return userRepo.findAll()
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }

    fun distance(
        lat1: Double, lat2: Double, lon1: Double,
        lon2: Double,
    ): Double {
        val R = 6371 // Radius of the earth
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters
        distance = Math.pow(distance, 2.0)
        return Math.sqrt(distance)
    }

    @PostMapping("/createNotification/{user}")
    @ResponseBody
    fun createNotification(
        @RequestBody notificationDto: NotificationDto,
        @PathVariable(value = "user") user: String
    ): Int? {
        try {
            var user: User? = userRepo.getUserByUuid(user)
            if (user != null) {
                var users: List<User>? = userRepo.findByLocationIsNotNull()
                if (users != null) {
                    if (notificationDto.location != null)
                        users.forEach { u ->
                            if (distance(
                                    notificationDto.location.lat,
                                    user.location!!.lat,
                                    notificationDto.location.lon,
                                    user.location!!.lon
                                ) > 0
                            ) {
                                val location = locationRepo.save(
                                    Location(
                                        lon = notificationDto.location.lon,
                                        lat = notificationDto.location.lat
                                    )
                                )
                                val notifications = Notifications(
                                    body = notificationDto.body,
                                    location = location,
                                    sender = user.uuid, receiver = u.uuid,
                                    type = NotificationType.valueOf(notificationDto.type),
                                    time = System.currentTimeMillis(),
                                    fileInfo = notificationDto.fileInfo
                                )
                                val res = notificationRepo.save(notifications);
                                messageService.sendNotification(notifications = res, to = u.uuid)
                            }


                        }
                }


            }


        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }

    @GetMapping("/getAllNotifications/{user}")
    @ResponseBody
    fun getAllNotifications(@PathVariable(value = "user") user: String): List<Notifications>? {
        try {
            val user: User? = userRepo.getUserByUuid(user)
            if (user != null) {
                return notificationRepo.getBySenderOrReceiver(user, user)
            }
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }


    @GetMapping("/getFriends/{user}")
    @ResponseBody
    fun getFriends(@PathVariable(value = "user") user: String): List<String>? {
        try {
            val user: User? = userRepo.getUserByUuid(user)
            val res = friendRepo.findByOwner(user!!)
            return res!!.map { s -> s.user.uuid }
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }

    @PostMapping("/shareLocation/{user}")
    fun shareLocation(
        @RequestBody location: com.example.dog_datting.models.Location,
        @PathVariable(value = "user") user: String
    ): ResponseEntity<String> {
        var u: User? = userRepo.getUserByUuid(user);
        if (u != null) {
            u.location = locationRepo.save(Location(lon = location.lon, lat = location.lat))
            userRepo.save(u)
        }
        return ResponseEntity.ok().build()
    }

    @GetMapping("/getUser/{user}")
    @ResponseBody
    fun getUser(@PathVariable(value = "user") user: String): User? {
        try {
            return userRepo.getUserByUuid(user)

        } catch (e: Exception) {
            logger.error(e)
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
            if (a.isPresent && a.get().owner.equals(user)) {
                animalRepo.deleteById(id)
                return ResponseEntity.ok().build()
            }

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }

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
                    avatarId = animalDto.avatarId
                )
            )
            return ResponseEntity.ok().build();
        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.internalServerError().build();
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
                    avatarId = animalDto.avatarId
                )
            )
            return ResponseEntity.ok().body(res.id)
        } catch (e: Exception) {
            logger.error(e)
        }
        return null

    }

    @GetMapping("/getGallery/{user}")
    @ResponseBody
    fun getGallery(@PathVariable(value = "user") user: String): List<Gallery>? {
        try {
            val user: User? = userRepo.getUserByUuid(user)
            return galleryRepo.getByUser(user!!)

        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }

    @PostMapping(path = ["/editInfo/{user}"])
    @ResponseBody
    fun editInfo(
        @RequestBody info: String,
        @PathVariable(value = "user") user: String,

        ): ResponseEntity<String?> {
        try {
            val o: User? = userRepo.getUserByUuid(user)
            if (o != null) {
                o.info = info
                userRepo.save(o)
            }

            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()

    }

    @PostMapping(path = ["/addFriend/{owner}/{user}"])
    @ResponseBody
    fun addFriend(
        @PathVariable(value = "owner") owner: String,
        @PathVariable(value = "user") frined: String
    ): ResponseEntity<String?> {
        try {
            val o: User? = userRepo.getUserByUuid(owner)
            val friend: User? = userRepo.getUserByUuid(frined)
            friendRepo.save(Friends(owner = o!!, user = friend!!))
            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()

    }


    @PostMapping(path = ["/updateGallery/{userId}"])
    @ResponseBody
    fun updateGallery(
        @RequestBody galleryDto: GalleryDto,
        @PathVariable(value = "userId") userId: String
    ): ResponseEntity<Int?> {
        try {
            val user: User? = userRepo.getUserByUuid(userId)
            if (user != null) {
                var res = galleryRepo.save(
                    Gallery(
                        user = userId,
                        time = System.currentTimeMillis(),
                        comment = galleryDto.comment,
                        fileInfo = galleryDto.fileInfo
                    )
                )
                return ResponseEntity.ok().body(res.id.toInt())
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()
    }


    @PostMapping(path = ["/deleteGallery/{user}/{id}"])
    @ResponseBody
    fun deleteGallery(

        @PathVariable(value = "user") user: String,
        @PathVariable(value = "id") id: Long
    ): ResponseEntity<Int?> {
        try {
            val gallery: Optional<Gallery> = galleryRepo.findById(id)
            if (gallery.isPresent && gallery.get().user == user) {
                galleryRepo.deleteById(id)
                return ResponseEntity.ok().build()
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()
    }
}