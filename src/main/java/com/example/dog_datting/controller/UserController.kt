package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.UserId
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.EmailService
import com.example.dog_datting.services.NotificationService
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
    private val emailService: EmailService,
    private val locationRepo: LocationRepo,
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
                    ResponseEntity.ok(UserId(id = user.uuid, username = user.username, name = user.firstname))
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
                logger.info("user is Exit.....")
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
    fun getAllUser(): List<User>? {
        try {
            return userRepo.findAll()
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
}