package com.example.dog_datting.controller

import com.example.dog_datting.db.*
import com.example.dog_datting.db.Location
import com.example.dog_datting.dto.*
import com.example.dog_datting.models.AdminRequestsRes
import com.example.dog_datting.repo.*
import com.example.dog_datting.services.PlaceService
import com.example.dog_datting.services.ShopService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class MainController(
    private val commentRepo: CommentRepo,
    private val settingRepo: SettingRepo,
    private val storyRepo: StoryRepo
) {

    val logger: Logger = LogManager.getLogger(MainController::class.java)


    @GetMapping(path = ["/fetchComments/{postId}"])
    fun fetchComments(@PathVariable("postId") postId: Int): List<Comment>? {
        try {
            return commentRepo.getCommentByPostIdOrderByTimeDesc(postId = postId.toString())
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null

    }

    @PostMapping(path = ["/updateSettings"])
    fun updateSetting(@RequestBody settingsDto: SettingsDto): ResponseEntity<String> {
        return try {
            var record = settingRepo.findById(1)
            if (record.isPresent) {
                settingRepo.save(
                    record.get().copy(showAd = settingsDto.showAd, adLoadingTimer = settingsDto.adLoadingTimer)
                )

            } else {
                settingRepo.save(
                    Settings(
                        id = 1,
                        showAd = settingsDto.showAd,
                        adLoadingTimer = settingsDto.adLoadingTimer
                    )
                )

            }
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().body(e.message)
        }
    }

    @GetMapping("/fetchSettings")
    fun fetchSettings(): Settings? {
        return settingRepo.findById(1).get()
    }


    @PostMapping(path = ["/saveStory"])
    @ResponseBody
    fun saveStory(@RequestBody storyDto: StoryDto): ResponseEntity<String>? {
        return try {
            storyRepo.save(
                Story(
                    userId = storyDto.userId,
                    fileInfo = storyDto.fileInfo,
                    description = storyDto.description,
                    time = storyDto.time
                )
            )
            return ResponseEntity.ok().build()

        } catch (e: Exception) {
            logger.error(e.message)
            ResponseEntity.internalServerError().build()
        }

    }

    @GetMapping(path = ["/fetchStory/{userId}"])
    @ResponseBody
    fun fetchStory(@PathVariable(value = "userId") userId: String): Story? {
        try {
            return storyRepo.findById(userId).get()
        } catch (e: Exception) {
            logger.error(e.message)
        }
        return null
    }
}