package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.AnimalType
import com.example.dog_datting.models.Gender
import com.example.dog_datting.models.UserId
import com.example.dog_datting.models.UserRes
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.EmailService
import com.example.dog_datting.services.LocationService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class UserController(
    private val userRepo: UserRepo,
    private val emailService: EmailService,
    private val locationRepo: LocationRepo,
    private val locationService: LocationService,
    private val animalRepo: AnimalRepo
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
                    user.username = user.firstname;
                    ResponseEntity.ok(
                        UserId(
                            id = user.uuid,
                            username = user.username,
                            name = user.firstname,
                            isAdmin = user.isAdmin
                        )
                    )
                } else {
                    ResponseEntity.badRequest().build()
                }
            } else {
                ResponseEntity.notFound().build()
            }

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

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
//                userService.sendVerificationCodeToPhone(loginByPhoneNumberDto.phoneNumber, code.toString())
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
                logger.info("user is exit.....")
                ResponseEntity.badRequest().build()
            } else {
                val code = kotlin.random.Random.nextInt(10000, 99999)
                if (u == null) {
                    val user = User()
                    logger.info("verification cede\t$code")
                    user.email = loginByEmailDto.email
                    user.username = loginByEmailDto.username
                    user.firstname = loginByEmailDto.name
                    user.password = loginByEmailDto.password
                    user.verificationCode = code
                    emailService.sendLoginCodeEmail(email = loginByEmailDto.email, code = code.toString())
                    userRepo.save(user)
                } else {
                    logger.info("user not null")
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

    @GetMapping("/sendActivity/{user}")
    @ResponseBody
    fun sendActivity(
        @PathVariable(value = "user") uid: String
    ): ResponseEntity<String?> {
        try {
            val user: User? = userRepo.getUserByUuid(uid)
            if (user != null) {
                userRepo.save(user.copy(lastConnectionTime = System.currentTimeMillis()));
                return ResponseEntity.ok().build()
            }
            return ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            return ResponseEntity.internalServerError().build();
        }

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
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build();

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

            return if (u != null) {
                if (u.verificationCode == verificationDto.code) {
                    val uuid = UUID.randomUUID().toString();
                    u.uuid = uuid
                    userRepo.save(u)
                    ResponseEntity.ok().body(u.uuid)
                }
                ResponseEntity.badRequest().build()
            } else {
                ResponseEntity.notFound().build()
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
    fun getAllUser(): List<UserRes>? {
        try {
            return (userRepo.findAll().map { u -> getUserRes(u) })
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
    fun getUser(@PathVariable(value = "user") user: String): UserRes? {
        try {
            return getUserRes(userRepo.getUserByUuid(user)!!)
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }

    @PostMapping(path = ["/blockUser/{requester}/{user}"])
    @ResponseBody
    fun blockUser(@PathVariable(value = "requester") requester: String, @PathVariable(value = "user") user: String) {

    }

    @PostMapping(path = ["/setFirebaseToken/{user}"])
    @ResponseBody
    fun setFirebaseToken(
        @RequestBody token: String,
        @PathVariable(value = "user") user: String,

        ): ResponseEntity<String?> {
        try {
            val o: User? = userRepo.getUserByUuid(user)
            if (o != null) {
                o.firebaseToken = token
                userRepo.save(o)
                return ResponseEntity.ok().build()
            }

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()

    }

    @PostMapping(path = ["/updateCertified/{userId}"])
    @ResponseBody
    fun updateCertified(
        @RequestBody certified: String,
        @PathVariable(value = "userId") userId: String,
    ): ResponseEntity<String?> {
        try {
            val user: User? = userRepo.getUserByUuid(userId)
            if (user != null) {
                user.certified = certified
                userRepo.save(user)
                return ResponseEntity.ok().build()
            }

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()

    }


    @PostMapping(path = ["/updateCasually/{userId}"])
    @ResponseBody
    fun updateCasually(
        @RequestBody value: String,
        @PathVariable(value = "userId") userId: String,

        ): ResponseEntity<String?> {
        try {
            val user: User? = userRepo.getUserByUuid(userId)
            if (user != null) {
                user.casually = value
                userRepo.save(user)
                return ResponseEntity.ok().build()
            }

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()

    }


    @PostMapping(path = ["/updateLook/{userId}"])
    @ResponseBody
    fun updateLook(
        @RequestBody updateLookReq: UpdateLookReq,
        @PathVariable(value = "userId") userId: String,

        ): ResponseEntity<String?> {
        try {
            val user: User? = userRepo.getUserByUuid(userId)
            if (user != null) {
                user.mate = updateLookReq.mate
                user.walk = updateLookReq.walk
                user.playingPartner = updateLookReq.playingPartner
                userRepo.save(
                    user
                )
                return ResponseEntity.ok().build()
            }

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()
    }


    @PostMapping("/searchInUsers")
    @ResponseBody
    fun searchInUsers(@RequestBody searchUserDto: SearchUserDto): List<UserRes>? {
        try {
            val users = userRepo.findByLocationIsNotNull()
            if (users != null) {
                return users.filter { u ->
                    locationService.checkLocation(
                        u.location!!,
                        Location(lat = searchUserDto.location.lat, lon = searchUserDto.location.lon)
                    )

                }.filter { u ->
                    (u.mate && searchUserDto.looking == "Mate") ||
                            (u.walk && searchUserDto.looking == "Walk") ||
                            (u.playingPartner && searchUserDto.looking == "Play")
                            ||
                            (searchUserDto.looking == "Certified pet-sitter" && u.certified.contains(
                                searchUserDto.have.name
                            )) || (searchUserDto.looking == "Private non-certified pet-sitter" && u.casually.contains(
                        searchUserDto.have.name
                    ))
                }.filter { u ->
                    if (searchUserDto.gender.isEmpty() || searchUserDto.gender == "") {
                        u.uuid.isNotEmpty()
                    } else {
                        checkAnimalAndGender(u.uuid, searchUserDto.have, Gender.valueOf(searchUserDto.gender))
                    }

                }.map { u -> getUserRes(u) }
            }


        } catch (e: Exception) {
            logger.error(e)
        }
        return null

    }


    fun checkAnimalAndGender(userId: String, animalType: AnimalType, gender: Gender): Boolean {
        try {
            val animals = animalRepo.getByOwner(userId) ?: return false
            return !animals.none { a -> a.type == animalType && a.sex == gender }
        } catch (e: Exception) {
            logger.error(e)
            return false
        }

    }

    fun getUserRes(user: User): UserRes {
        return UserRes(
            username = user.username,
            firstname = user.firstname,
            info = user.info,
            uuid = user.uuid,
            walk = user.walk,
            mate = user.mate,
            playingPartner = user.playingPartner,
            certified = user.certified,
            casually = user.casually,
            interests = user.interests
        )
    }
}