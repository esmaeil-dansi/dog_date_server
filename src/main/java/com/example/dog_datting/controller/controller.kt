package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.db.Location
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.*
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.EmailService
import com.example.dog_datting.services.MessageService
import com.example.dog_datting.services.UserService
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class MainController(
    private val userService: UserService,
    private val chatRepo: ChatRepo
) {

    val logger: Logger = LogManager.getLogger(MainController::class.java)

    @Autowired
    private lateinit var userRepo: UserRepo

    @Autowired
    private lateinit var locationRepo: LocationRepo

    @Autowired
    private lateinit var shopRepo: ShopRepo

    @Autowired
    private lateinit var postRepo: PostRepo

    @Autowired
    private lateinit var doctorRepo: DoctorRepo

    @Autowired
    private lateinit var shopItemRepo: ShopItemRepo

    @Autowired
    private lateinit var fileInfoRepo: FileInfoRepo


    @Autowired
    private lateinit var emailService: EmailService


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
            logger.info("singing by email...............")
            val u: User? = userRepo.getUserByEmail(loginByEmailDto.email)

            return if (u != null && u.uuid.isEmpty()) {
                ResponseEntity.badRequest().build()
            } else {
                val code = kotlin.random.Random.nextInt(10000, 99999)

                if (u == null) {
                    val user = User()
                    logger.info("verification cede\t" + code)
                    user.email = loginByEmailDto.email
                    user.password = loginByEmailDto.password
                    user.verificationCode = code
                    emailService.sendLoginCodeEmail(email = loginByEmailDto.email, code = code.toString())
                    userRepo.save(user)
                } else {
                    u.verificationCode = code
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

    @GetMapping("/recoveryEmail/{email}")
    @ResponseBody
    fun sendRecoveryEmail(@PathVariable(value = "email") email: String): ResponseEntity<String> {
        var user: User? = userRepo.getUserByEmail(email = email)
        if (user != null) {
            val recoverCode = kotlin.random.Random.nextInt(10000, 99999)
            user.recoveryCode = recoverCode
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

    @PostMapping(path = ["/login"]) //
    @ResponseBody
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<String> {
        try {
            var user: User? = null
            if (loginDto.email.isNotEmpty()) {
                user = userRepo.getUserByEmail(loginDto.email)

            }
            return if (user != null) {
                if (user.password == loginDto.password) {
                    ResponseEntity.ok(user.uuid)
                } else {
                    ResponseEntity.badRequest().body("NOT_MATCH")
                }

            } else {
                ResponseEntity.notFound().build()
            }

        } catch (ignored: Exception) {
        }
        return ResponseEntity.internalServerError().build()

    }


    @PostMapping(path = ["/saveShop"]) //
    @ResponseBody
    fun saveShop(@RequestBody body: String?): String {
        val gson = Gson() // Or use new GsonBuilder().create();
        val target2 = gson.fromJson(body, Post::class.java)
        val user = User()
        //        user.setFirstname(name);
//        n.setLastname(email);
//        userRepo.save(n);
        return "Saved"
    }


    @PostMapping(path = ["/savePost"]) //
    @ResponseBody
    fun savePost(@RequestBody newPostDao: NewPostDao): ResponseEntity<SavePostRes?> {
        try {
            var time: Long = System.currentTimeMillis();
            val post = Post()
            post.description = newPostDao.description
            post.type = getPostType(newPostDao.type)
            post.title = newPostDao.title
            post.ownerId = newPostDao.ownerId
            post.fileUuid = newPostDao.fileUuid
            post.time = time
            val id = postRepo.save(post)
            return ResponseEntity.ok().body(SavePostRes(time = time, id = id.id.toInt()))
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }

    private fun getPostType(key: String): PostType {
        when (key) {
            "DENGER" -> return PostType.DENGER
            "BAY" -> return PostType.BAY
            "LOSED" -> return PostType.LOSED
            "PAIRING" -> return PostType.PAIRING
            "MAINTENANCE" -> return PostType.MAINTENANCE
            "SALE" -> return PostType.SALE
        }
        return PostType.SALE
    }

    @GetMapping("fetchChats/{uuid}")
    @ResponseBody
    fun fetchChat(@PathVariable(value = "uuid") uuid: String): List<ChatResult>? {
        try {
            var res: MutableList<ChatResult> = mutableListOf()
            chatRepo.getByOwnerId(uuid)?.forEach { chat ->
                run {
                    res.add(
                        ChatResult(
                            message = chat.lastMessage,
                            userId = chat.userId,
                            name = "",
                            lastTime = chat.lastTime
                        )
                    )
                }
            }
            return res;


        } catch (e: Exception) {
            print(e.message)
        }
        return null
    }


    @GetMapping(path = ["/fetchPost/{lastPostId}"])
    fun fetchPost(@PathVariable("lastPostId") lastPostId: Int): List<PostRes> {
        val posts = postRepo.findByIdGreaterThanOrderByIdDesc(lastPostId.toLong())
        val postResList: MutableList<PostRes> = ArrayList()
        if (posts != null) {
            for (post in posts) {
                val postRes = PostRes()
                postRes.description = post.description
                postRes.id = post.id
                postRes.title = post.title
                postRes.time = post.time
                postRes.ownerId = post.ownerId
                postRes.type = post.type
                val info = fileInfoRepo.getByPacketId(post.fileUuid)
                if (info != null) {
                    postRes.fileUuids = info.map { f -> f.uuid }
                }
                postResList.add(postRes)
            }
        }

        return postResList
    }

    @PostMapping(path = ["/saveDoctor"]) //
    @ResponseBody
    fun saveDoctor(@RequestBody body: String?): String {
        val user = User()
        //        user.setFirstname(name);
//        n.setLastname(email);
//        userRepo.save(n);
        return "Saved"
    }

    @GetMapping(path = ["/getDoctor/{doctorId}"])
    fun getDoctor(@PathVariable("doctorId") pointer: Int?): User {
        return User()
        //        return userRepo.getUserByUuid(userUid);
    }
}