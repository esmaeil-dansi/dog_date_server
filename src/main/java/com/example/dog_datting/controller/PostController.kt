package com.example.dog_datting.controller

import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Post
import com.example.dog_datting.db.PostLikes
import com.example.dog_datting.dto.NewPostDao
import com.example.dog_datting.models.PostRes
import com.example.dog_datting.models.PostType
import com.example.dog_datting.models.SavePostRes
import com.example.dog_datting.repo.FileInfoRepo
import com.example.dog_datting.repo.LocationRepo
import com.example.dog_datting.repo.PostLikesRepo
import com.example.dog_datting.repo.PostRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.ArrayList

@RestController
class PostController(
    private val postRepo: PostRepo,
    private val fileInfoRepo: FileInfoRepo,
    private val locationRepo: LocationRepo,
    private val postLikesRepo: PostLikesRepo
) {
    val logger: Logger = LogManager.getLogger(MainController::class.java)

    @GetMapping(path = ["/fetchPost/{requester}/{lastPostId}"])
    fun fetchPost(
        @PathVariable("lastPostId") lastPostId: Int,
        @PathVariable("requester") requester: String
    ): List<PostRes> {
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
                postRes.location =
                    com.example.dog_datting.models.Location(lat = post.location.lat, lon = post.location.lon)
                val info = fileInfoRepo.getByPacketId(post.fileUuid)
                if (info != null) {
                    postRes.fileUuids = info.map { f -> f.uuid }
                }
                postRes.likes = post.likesCount
                postRes.myFavorite = (postLikesRepo.getByUserIdAndPost(userId = requester, post = post) != null)
                if (post.locationInfo != null) {
                    postRes.locationInfo = com.example.dog_datting.models.Location(
                        lat = post.locationInfo!!.lat,
                        lon = post.locationInfo!!.lon
                    )
                }
                postResList.add(postRes)
            }
        }

        return postResList
    }

    @PostMapping("/likePost/{postId}/{userId}")
    fun likePost(@PathVariable("postId") postId: Int, @PathVariable("userId") userId: String) {
        try {
            var post = postRepo.findById(postId)
            if (post.isPresent) {
                val p = post.get()
                var like = postLikesRepo.getByUserIdAndPost(post = post.get(), userId = userId)
                if (like == null) {
                    postLikesRepo.save(PostLikes(post = post.get(), userId = userId))
                    p.likesCount++
                } else {
                    postLikesRepo.delete(like)
                    if (p.likesCount > 0) {
                        p.likesCount--
                    }
                }
                postRepo.save(p)
            }


        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    @PostMapping(path = ["/savePost"])
    @ResponseBody
    fun savePost(@RequestBody newPostDao: NewPostDao): ResponseEntity<SavePostRes?> {
        try {
            val location = locationRepo.save(Location(lon = newPostDao.location.lon, lat = newPostDao.location.lat))

            val time: Long = System.currentTimeMillis();
            val post = Post()
            post.description = newPostDao.description
            post.type = getPostType(newPostDao.type)
            post.title = newPostDao.title
            post.ownerId = newPostDao.ownerId
            post.location = location
            post.fileUuid = newPostDao.fileUuid
            post.time = time
            if (newPostDao.locationInfo != null) {
                val locationInfo =
                    locationRepo.save(Location(lon = newPostDao.locationInfo.lon, lat = newPostDao.locationInfo.lat))
                post.locationInfo = locationInfo

            }
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


    @PostMapping(path = ["/deletePost"])
    @ResponseBody
    fun deletePost(@RequestBody id: Int): ResponseEntity<String?> {
        return try {
            postRepo.deleteById(id)
            ResponseEntity.ok().build();
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build();
        }
    }
}