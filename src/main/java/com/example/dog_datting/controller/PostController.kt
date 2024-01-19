package com.example.dog_datting.controller

import com.example.dog_datting.db.Location
import com.example.dog_datting.db.Post
import com.example.dog_datting.db.PostLikes
import com.example.dog_datting.dto.NewPostDao
import com.example.dog_datting.models.PostRes
import com.example.dog_datting.models.PostType
import com.example.dog_datting.models.SavePostRes
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.PostService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class PostController(
    private val postRepo: PostRepo,
    private val fileInfoRepo: FileInfoRepo,
    private val locationRepo: LocationRepo,
    private val postLikesRepo: PostLikesRepo,
    private val userRepo: UserRepo,
    private val postService: PostService
) {
    val logger: Logger = LogManager.getLogger(MainController::class.java)

    @GetMapping(path = ["/fetchPost/{requester}/{lastPostId}"])
    fun fetchPost(
        @PathVariable("lastPostId") lastPostId: Int,
        @PathVariable("requester") requester: String
    ): List<PostRes>? {
        val user = userRepo.getUserByUuid(requester)
        if (user != null) {
            return if (user.location != null) {
                val posts = postRepo.findByIdGreaterThanOrderByIdDesc(lastPostId.toLong())
                mapPosts(posts?.filter { p -> postService.checkLocation(user.location!!, p) }, requester)
            } else {
                mapPosts(postRepo.findByIdGreaterThanOrderByIdDesc(lastPostId.toLong()), requester);
            }
        }
        return ArrayList()
    }

    @GetMapping(path = ["/getPostById/{id}"])
    fun getPostById(
        @PathVariable("id") id: Long,
    ): PostRes? {
        return mapPosts(listOf(postRepo.findById(id).get()), "").firstOrNull()
    }

    fun mapPosts(posts: List<Post>?, requester: String): List<PostRes> {
        val postResList: MutableList<PostRes> = ArrayList()
        if (posts != null) {
            for (post in posts) {
                val postRes = PostRes()
                postRes.description = post.description
                postRes.id = post.id
                postRes.title = post.title
                postRes.time = post.time
                postRes.topics = post.topics.split(",")
                postRes.ownerId = post.ownerId
                postRes.type = post.type
                postRes.location =
                    com.example.dog_datting.models.Location(lat = post.location.lat, lon = post.location.lon)
                val info = fileInfoRepo.getByPacketId(post.fileUuid)
                if (info != null) {
                    postRes.fileUuids = info.map { f -> f.uuid }
                }
                postRes.likes = post.likesCount
                postRes.commentsCount = post.commentsCount
                if (requester.isNotEmpty())
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
    fun likePost(@PathVariable("postId") postId: Long, @PathVariable("userId") userId: String) {
        try {
            var post = postRepo.findById(postId)
            if (post.isPresent) {
                val p = post.get()
                var like = postLikesRepo.getByUserIdAndPost(post = post.get(), userId = userId)

                if (like == null) {
                    logger.info("like not found !!!!!!!!!!!")
                    postLikesRepo.save(PostLikes(post = post.get(), userId = userId))
                    p.likesCount++
                } else {
                    logger.info("like  found !!!!!!!!!!!")
                    postLikesRepo.delete(like)
                    if (p.likesCount > 0) {
                        p.likesCount--
                    }
                }
                logger.info("save likeeeee")
                postRepo.save(p)
            } else {
                logger.info("post not found !!!!!!!!!!!")
            }


        } catch (e: Exception) {
            logger.error(e.message)
        }

    }

    @PostMapping(path = ["/savePost"])
    @ResponseBody
    fun savePost(@RequestBody newPostDao: NewPostDao): ResponseEntity<SavePostRes?> {
        try {
            val time: Long = System.currentTimeMillis();
            val post = Post()
            post.description = newPostDao.description
            post.type = getPostType(newPostDao.type)
            post.title = newPostDao.title
            post.ownerId = newPostDao.ownerId
            post.location = locationRepo.save(Location(lon = newPostDao.location.lon, lat = newPostDao.location.lat))
            post.topics = newPostDao.topics.joinToString()
            post.fileUuid = newPostDao.fileUuid
            post.time = time
            if (newPostDao.locationInfo != null) {
                val locationInfo =
                    locationRepo.save(Location(lon = newPostDao.locationInfo.lon, lat = newPostDao.locationInfo.lat))
                post.locationInfo = locationInfo

            }
            val savePost = postRepo.save(post)


            postService.processPost(savePost);

            return ResponseEntity.ok().body(SavePostRes(time = time, id = savePost.id.toInt()))
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return ResponseEntity.internalServerError().build()

    }

    private fun getPostType(key: String): PostType {
        try {
            return PostType.valueOf(key)
        } catch (e: Exception) {
            logger.info(key)
            logger.error(e)

        }
        return PostType.DANGER
    }


    @GetMapping(path = ["/deletePost/{id}"])
    @ResponseBody
    fun deletePost(@PathVariable("id") id: Long): ResponseEntity<String?> {
        return try {
            var post = postRepo.findById(id)
            postLikesRepo.getByPost(post.get())?.forEach { p ->
                postLikesRepo.delete(p)
            }
            postRepo.delete(post.get())
            ResponseEntity.ok().build();
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build();
        }
    }
}