package com.example.dog_datting.controller

import com.example.dog_datting.db.Friends
import com.example.dog_datting.db.Gallery
import com.example.dog_datting.db.Topics
import com.example.dog_datting.db.User
import com.example.dog_datting.dto.GalleryDto
import com.example.dog_datting.repo.FriendRepo
import com.example.dog_datting.repo.GalleryRepo
import com.example.dog_datting.repo.TopicRepo
import com.example.dog_datting.repo.UserRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ProfileController(
    private val topicRepo: TopicRepo,
    private val userRepo: UserRepo,
    private val friendRepo: FriendRepo,
    private val galleryRepo: GalleryRepo,
) {

    val logger: Logger = LogManager.getLogger(UserController::class.java)

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


    @GetMapping("/getFriends/{requester}")
    @ResponseBody
    fun getFriends(@PathVariable(value = "requester") requester: String): List<User>? {
        try {
            val user: User? = userRepo.getUserByUuid(requester)
            var res1 = friendRepo.findByOwner(user!!)
            val res2 = friendRepo.findByUser(user)
            var res: List<User> = listOf()
            if (res1 != null) {
                res = res1.map { d -> d.user }
            }
            if (res2 != null) {
                res = res + res2.map { f -> f.owner }
            }
            return res
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }

    @GetMapping("/getGallery/{requester}")
    @ResponseBody
    fun getGallery(@PathVariable(value = "requester") requester: String): List<Gallery>? {
        try {
            return galleryRepo.getByUser(requester)
        } catch (e: Exception) {
            logger.error(e)
        }
        return null
    }

    @PostMapping(path = ["/editInterests/{user}"])
    @ResponseBody
    fun editInterests(
        @RequestBody info: String,
        @PathVariable(value = "user") user: String,

        ): ResponseEntity<String?> {
        try {
            val o: User? = userRepo.getUserByUuid(user)
            if (o != null) {
                o.interests = info
                userRepo.save(o)
            }

            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e)
        }
        return ResponseEntity.badRequest().build()

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
            val res = galleryRepo.save(
                Gallery(
                    user = userId,
                    time = System.currentTimeMillis(),
                    comment = galleryDto.comment,
                    fileInfo = galleryDto.fileInfo
                )
            )
            return ResponseEntity.ok().body(res.id.toInt())

        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()
    }


    @GetMapping(path = ["/deleteGallery/{user}/{id}"])
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