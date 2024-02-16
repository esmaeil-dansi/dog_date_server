package com.example.dog_datting.controller

import com.example.dog_datting.db.Story
import com.example.dog_datting.dto.StoryDto
import com.example.dog_datting.repo.StoryRepo
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.GetMapping


@Controller
class MainController(

    private val storyRepo: StoryRepo
) {

    val logger: Logger = LogManager.getLogger(MainController::class.java)

    @GetMapping("/privacy")
    fun privacy(model: Model): String {
        return "privacy"
    }

    @GetMapping("/terms_of_use")
    fun terms(model: Model): String {
        return "terms_of_use"
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